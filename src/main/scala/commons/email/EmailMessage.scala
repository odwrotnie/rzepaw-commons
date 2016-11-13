package commons.email

import java.io.{InputStream, ByteArrayInputStream, File}
import java.text.SimpleDateFormat
import javax.activation.DataHandler
import javax.mail.BodyPart
import javax.mail.Flags
import javax.mail.Folder
import javax.mail.Session
import javax.mail._
import javax.mail.internet.MimeMultipart
import javax.mail.internet._
import javax.mail.search._
import java.util.Properties

import com.sun.mail.imap.IMAPBodyPart
import com.sun.mail.imap.IMAPInputStream
import com.sun.mail.imap.IMAPMessage
import com.sun.mail.imap.{IMAPInputStream, IMAPBodyPart, IMAPMessage}
import commons.text.Pattern
import org.joda.time.DateTime

import scala.collection
import scala.collection.parallel.mutable
import scala.util.Try
import scala.xml.NodeSeq

object IMAP {
  val PROTOCOL = "mail.store.protocol"
  val IMAPS = "imaps"
}

abstract class EmailMessage {
  val HTML_START = "<html"
  val THREAD_PATTERN = "\\[[^\\[\\]]+\\]".r
  val IN_THREAD_PATTERN = "[^\\[\\]]+".r
  def identity: String
  def date: DateTime
  def senderAddress: String
  def senderName: String
  def recipients: Seq[String]
  def recipientsCC: Seq[String]
  def recipientsBCC: Seq[String]
  def subjectRaw: String
  def subjectThread: Option[String] = Pattern.pickFirst(THREAD_PATTERN, IN_THREAD_PATTERN)(subjectRaw)
  def subject: String = subjectRaw//.replaceFirst(THREAD_PATTERN.regex, "").replaceAll("\\s+", " ").trim
  def text: Option[String]
  def html: Option[String]
  def files: List[Attachment]
  override def toString = s"$subject:(${ text.take(10) }...})"
}

case class Attachment(name: String, file: Array[Byte])

