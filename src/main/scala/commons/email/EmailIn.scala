package commons.email

import java.io.InputStream
import java.text.SimpleDateFormat
import javax.mail.Message.RecipientType
import javax.mail.{Session, Folder, Flags, BodyPart}
import javax.mail.internet.MimeMultipart

import com.sun.mail.imap.{IMAPBodyPart, IMAPMessage}
import commons.logger.Logger
import commons.text.Pattern
import org.joda.time.DateTime

import scala.util.Try

case class IMAPServer(server: String,
                      username: String,
                      password: String,
                      folderName: String,
                      port: Int = 993)
  extends Logger {

  val store = {
    val session = {
      val props = System.getProperties()
      props.setProperty(IMAP.PROTOCOL, IMAP.IMAPS)
      Session.getDefaultInstance(props, null)
    }
    session.getStore(IMAP.IMAPS)
  }

  val folder: Option[Folder] = Try {
    store.connect(server, username, password)
    val f = store.getFolder(folderName)
    f.open(Folder.READ_WRITE)
    f
  }.toOption

  def close = {
    folder.foreach(_.close(true))
    store.close()
  }

  def messages: List[EmailIn] = folder match {
    case Some(folder) =>
      val messages = folder.getMessages()
      debug(s"Folder ($folder) messages: ${ messages.size }")
      messages.map(m => EmailIn(m.asInstanceOf[IMAPMessage])).reverse.toList
    case _ =>
      warn(s"Authentication error for: $username or no folder named: $folderName")
      Nil
  }

  def messagesUnseen: List[EmailIn] = messages.filterNot(_.seen)
}

case class EmailIn(m: IMAPMessage)
  extends EmailMessage {

  lazy val identity = m.getMessageID
  lazy val date = new DateTime(m.getReceivedDate)
  val ADDRESS_PATTERN = "<.+>".r
  val IN_ADDRESS_PATTERN = "[^<>]+".r
  lazy val sender = Pattern.pickFirst(ADDRESS_PATTERN, IN_ADDRESS_PATTERN)(m.getSender.toString).get

  lazy val subjectRaw = m.getSubject
  lazy val contentType = m.getContentType
  lazy val dateSent = m.getSentDate

  private lazy val strings: List[String] = content.filter(_.isInstanceOf[String]).map(_.asInstanceOf[String].trim)
  lazy val text: Option[String] = strings.filter(!_.startsWith(HTML_START)).headOption
  lazy val html: Option[String] = strings.filter(_.startsWith(HTML_START)).headOption
  lazy val files: List[Attachment] = content.filter(_.isInstanceOf[Attachment]).map(_.asInstanceOf[Attachment])

  lazy val content = {
    def getContent(content: Object, source: Option[Object]): List[AnyRef] = {
      content match {
        case im: IMAPMessage => getContent(im.getContent, Some(im))
        case m: MimeMultipart => (0 until m.getCount).map(i => m.getBodyPart(i)).flatMap(bp => getContent(bp, Some(m))).toList
        case ibp: IMAPBodyPart => getContent(ibp.getContent, Some(ibp))
        case bp: BodyPart => getContent(bp.getContent, Some(bp))
        case is: InputStream =>
          val fileName = source.map(_.asInstanceOf[IMAPBodyPart].getFileName).getOrElse("F")
          val bytes = Stream.continually(is.read).takeWhile(-1 !=).map(_.toByte).toArray
          List[Attachment](Attachment(fileName, bytes))
        case s: String => List(s)
        case o => List(o)
      }
    }
    getContent(m, None)
  }

  def seen = m.isSet(Flags.Flag.SEEN)
  def see = m.setFlag(Flags.Flag.SEEN, true)
  def unSee = m.setFlag(Flags.Flag.SEEN, false)

  def delete_! = m.setFlag(Flags.Flag.DELETED, true)

  def recipients: Seq[String] = m.getRecipients(RecipientType.TO).map(_.toString)
  def recipientsCC: Seq[String] = m.getRecipients(RecipientType.CC).map(_.toString)
  def recipientsBCC: Seq[String] = m.getRecipients(RecipientType.BCC).map(_.toString)
  def senderAddress: String = m.getSender.toString
  def senderName: String = senderAddress

  def move(folder: String): Unit = {
    val f = m.getFolder.getParent.getFolder(folder)
    if(!f.exists())
      f.create(Folder.HOLDS_MESSAGES)
    m.getFolder.copyMessages(Array(m), f)
    delete_!
  }

  override def toString = {
    val df = new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss")
    s"${if (seen) "( )" else "(*)"}[${df.format(dateSent)}] $subject"
  }
}
