package commons.text

import scala.util.Try

case class ParserChain[T](list: (String => T) *) {
  def parse(s: String): Option[T] = list.flatMap { st =>
    Try(st(s)).toOption
  }.headOption
}
