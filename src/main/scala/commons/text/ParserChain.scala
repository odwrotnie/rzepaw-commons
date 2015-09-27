package commons.text

import commons.logger.Logger

import scala.util.Try

case class ParserChain[T](list: (String => T) *)
  extends Logger {

  // TODO Przekazywać jako funkcje i obliczać do pierwszej poprawnej
  def apply(s: String): Option[T] = {
    val steps = list.map { st =>
      Try(st(s)).toOption
    }
    debug(s"Parsing: $s with ${ list.size } steps - ${ steps.mkString("[ ", " | ", " ]") }")
    steps.flatten.headOption
  }
}
