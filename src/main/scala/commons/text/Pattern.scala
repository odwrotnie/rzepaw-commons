package commons.text

import scala.util.Try
import scala.util.matching.Regex

object Pattern {

  lazy val INTEGER_PATTERN = "\\d+".r
  lazy val DOUBLE_PATTERN = "\\d+([\\.,]\\d+)?".r
  lazy val EMAIL_PATTERN = """([a-zA-Z0-9_\.\-\+])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+""".r

  // FIRST

  def pickFirst(patterns: Regex *)(text: String): Option[String] =
    patterns.foldLeft(Some(text): Option[String])(
      (s, p) => s.flatMap(s => p.findFirstIn(s))
    )

  def pickFirstString(s: String, text: String): Option[String] =
    pickFirst(s.r)(text)

  def pickFirstInteger(text: String): Option[Int] =
    Try(pickFirst(INTEGER_PATTERN)(text).get.toInt).toOption

  def pickFirstDouble(text: String): Option[Double] =
    Try(pickFirst(DOUBLE_PATTERN)(text).get.replace(",", ".").toDouble).toOption

  def pickFirstEmail(text: String): Option[String] =
    pickFirst(EMAIL_PATTERN)(text)

  // ALL

  def pickAll(pattern: Regex)(text: String): List[String] =
    pattern.findAllIn(text).toList

  def pickAllIntegers(text: String): List[Int] =
    pickAll(INTEGER_PATTERN)(text).map(_.toInt)

  def pickIntegers(text: String): Option[String] =
    pickAll(INTEGER_PATTERN)(text).mkString match {
      case s: String if s.nonEmpty => Some(s)
      case _ => None
    }

  def pickAllEmails(text: String): List[String] =
    pickAll(EMAIL_PATTERN)(text)
}
