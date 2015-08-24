package commons.text

import scala.util.matching.Regex

object Pattern {

  def pickFirst(patterns: Regex*)(text: String): Option[String] =
    patterns.foldLeft(Some(text): Option[String])(
      (s, p) => s.flatMap(s => p.findFirstIn(s))
    )
}
