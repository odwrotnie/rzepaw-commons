package commons.text

import scala.util.Try
import scala.util.matching.Regex

object Pattern {

  def pickFirst(patterns: Regex *)(text: String): Option[String] =
    patterns.foldLeft(Some(text): Option[String])(
      (s, p) => s.flatMap(s => p.findFirstIn(s))
    )

  lazy val INTEGER_PATTERN = "\\d+".r
  def pickFirstInteger(text: String): Option[Int] =
    Try(pickFirst(INTEGER_PATTERN)(text).get.toInt).toOption

  lazy val DOUBLE_PATTERN = "\\d+([\\.,]\\d+)?".r
  def pickFirstDouble(text: String): Option[Double] =
    Try(pickFirst(DOUBLE_PATTERN)(text).get.replace(",", ".").toDouble).toOption
}
