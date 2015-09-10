package commons.text

import commons.logger.Logger

import scala.util.Try

case class ParserChain[T](list: (String => T) *)
  extends Logger {
  def apply(s: String): Option[T] = {
    debug(s"Parsing: $s with ${ list.size } steps")
    list.flatMap { st =>
      Try(st(s)).toOption
    }.headOption
  }
}
