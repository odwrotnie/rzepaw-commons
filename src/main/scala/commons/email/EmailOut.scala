package commons.email

import org.apache.commons.mail._
import org.joda.time.DateTime
import rzepaw.configuration.Configuration

import scala.xml.{Elem, NodeSeq}

case class SMTPServer(address: String,
                      username: String,
                      password: String,
                      port: Int = 587,
                      isSSL: Boolean = false,
                      isTLS: Boolean = false,
                      ignoreSmtpSslTrust: Boolean = false) {

  implicit def stringToSeq(single: String): Seq[String] = Seq(single)
  implicit def liftToOption[T](t: T): Option[T] = Some(t)

  def configure(email: Email) {
    email.setHostName(address)
    email.setSmtpPort(port)
    email.setSslSmtpPort(port.toString)
    email.setAuthenticator(new DefaultAuthenticator(username, password))
    email.setSSLOnConnect(isSSL)
    email.setStartTLSEnabled(isTLS)
    if (ignoreSmtpSslTrust) {
      val props = System.getProperties
      props.put("mail.smtp.ssl.trust", "*")
    }
  }
}

object SMTPServer
  extends Configuration {

  def forConfig: SMTPServer = {
    val cfg = configuration.getConfig("email.smtp")
    val address = cfg.getString("address")
    val username = cfg.getString("username")
    val password = cfg.getString("password")
    val port = cfg.getInt("port")
    val isSSL = cfg.getBoolean("is.ssl")
    val isTLS = cfg.getBoolean("is.tls")
    val ignoreSmtpSslTrust = cfg.getBoolean("ignore.smtp.ssl.trust")
    SMTPServer(address, username, password, port,
      isSSL = isSSL, isTLS = isTLS,
      ignoreSmtpSslTrust = ignoreSmtpSslTrust)
  }
}

class EmailOut(smtp: SMTPServer)
  extends EmailMessage {

  sealed abstract class MailType
  case object Plain extends MailType
  case object Rich extends MailType
  case object MultiPart extends MailType

  var date = new DateTime(DateTime.now())

  var files: List[commons.email.Attachment] = Nil
  def html: Option[String] = List(htmlXml.map(_.toString()), htmlString).flatten.headOption
  var htmlXml: Option[NodeSeq] = None
  var htmlString: Option[String] = None
  var identity: String = "?"
  var recipients: Seq[String] = Seq()
  var recipientsBCC: Seq[String] = Seq()
  var recipientsCC: Seq[String] = Seq()
  var senderAddress: String = ""
  var senderName: String = ""
  var replyTo: Seq[String] = Seq()
  var subjectRaw: String = ""
  var text: Option[String] = None

  def send {

    require(text.isDefined)
    require(text.get.nonEmpty)

    date = DateTime.now()

    val format =
      if (!files.isEmpty) MultiPart
      else if (html.isDefined) Rich
      else Plain

    val email: Email = format match {
      case Plain =>
        new SimpleEmail().setMsg(text.get)
      case Rich =>
        val htmlString = "<!doctype html>\n" + html.get
        new HtmlEmail().setHtmlMsg(htmlString).setTextMsg(text.get)
      case MultiPart => {
        val mpe = new MultiPartEmail()
        mpe.setMsg(text.get)
        files.foreach { file =>
          val att = new EmailAttachment()
          // att.setPath(file.name)
          att.setDisposition(EmailAttachment.ATTACHMENT)
          att.setName(file.name)
          mpe.attach(att)
        }
        mpe
      }
    }

    smtp.configure(email)

    recipients foreach (email.addTo(_))
    recipientsCC foreach (email.addCc(_))
    recipientsBCC foreach (email.addBcc(_))

    replyTo foreach (email.addReplyTo(_))

    email.setFrom(senderAddress, senderName).
      setSubject(subject).
      send()
  }
}
