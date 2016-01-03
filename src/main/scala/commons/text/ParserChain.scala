package commons.text

import commons.logger.Logger

import scala.util.Try

case class ParserChain[T](s: String)
  extends Logger {

  var methods = collection.mutable.ListBuffer[(String => T)]()

  def +(f: (String => T)): ParserChain[T] = {
    methods += f
    this
  }

  def parse: Option[T] = {
    methods.toStream.map { st =>
      Try(st(s)).toOption
    }.find(_.isDefined).flatten
  }
}
