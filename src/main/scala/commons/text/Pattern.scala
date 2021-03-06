package commons.text

import scala.util.Try
import scala.util.matching.Regex

object Pattern {

  lazy val NO_DIGITS_REGEX = "[^0-9]+".r
  lazy val INTEGER_REGEX = "\\d+".r
  lazy val DOUBLE_REGEX = "\\d+([\\.,]\\d+)?".r

  lazy val EMAIL_PATTERN = """([a-zA-Z0-9_\.\-\+])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+"""
  lazy val EMAIL_REGEX = EMAIL_PATTERN.r

  // FIRST

  def pickFirst(patterns: Regex *)(text: String): Option[String] =
    patterns.foldLeft(Some(text): Option[String])(
      (s, p) => s.flatMap(s => p.findFirstIn(s))
    )

  def pickFirstString(s: String, text: String): Option[String] =
    pickFirst(s.r)(text)

  def pickFirstNoDigits(text: String): Option[String] =
    Try(pickFirst(NO_DIGITS_REGEX)(text).get).toOption

  def pickFirstInteger(text: String): Option[Int] =
    Try(pickFirst(INTEGER_REGEX)(text).get.toInt).toOption

  def pickFirstDouble(text: String): Option[Double] =
    Try(pickFirst(DOUBLE_REGEX)(text).get.replace(",", ".").toDouble).toOption

  def pickFirstEmail(text: String): Option[String] =
    pickFirst(EMAIL_REGEX)(text)

  // ALL

  def pickAll(pattern: Regex)(text: String): List[String] =
    pattern.findAllIn(text).toList

  def pickAllIntegers(text: String): List[Int] =
    pickAll(INTEGER_REGEX)(text).map(_.toInt)

  def pickIntegers(text: String): Option[String] =
    pickAll(INTEGER_REGEX)(text).mkString match {
      case s: String if s.nonEmpty => Some(s)
      case _ => None
    }

  def pickAllEmails(text: String): List[String] =
    pickAll(EMAIL_REGEX)(text)

  // VALIDATE

  def validateEmail(text: String): Boolean =
    text.matches(EMAIL_PATTERN)
}
