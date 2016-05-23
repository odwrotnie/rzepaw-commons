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
    }.find(_.isDefined).flatten match {
      case None =>
        warn(s"Unable to parse $s, returning None")
        None
      case s => s
    }
  }

  def parseOrElse(t: T): T = {
    warn(s"Unable to parse $s, returning default")
    parse.getOrElse(t)
  }

  def parseOrThrowException(message: String): T = {
    val m = s"Unable to parse $s, throwing exception - $message"
    error(m)
    parse match {
      case Some(t) => t
      case _ => throw new Exception(m)
    }
  }
}
