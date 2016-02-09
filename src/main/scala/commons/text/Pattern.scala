package commons.text

import scala.util.Try
import scala.util.matching.Regex

object Pattern {

  lazy val INTEGER_PATTERN = "\\d+".r
  lazy val DOUBLE_PATTERN = "\\d+([\\.,]\\d+)?".r

  // FIRST

  def pickFirst(patterns: Regex *)(text: String): Option[String] =
    patterns.foldLeft(Some(text): Option[String])(
      (s, p) => s.flatMap(s => p.findFirstIn(s))
    )


  def pickFirstInteger(text: String): Option[Int] =
    Try(pickFirst(INTEGER_PATTERN)(text).get.toInt).toOption

  def pickFirstDouble(text: String): Option[Double] =
    Try(pickFirst(DOUBLE_PATTERN)(text).get.replace(",", ".").toDouble).toOption

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
}
