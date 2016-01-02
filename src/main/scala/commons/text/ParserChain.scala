package commons.text

import commons.logger.Logger

import scala.util.Try

@deprecated("User the 2nd version")
case class ParserChain[T](list: (String => T) *)
  extends Logger {

  def apply(s: String): Option[T] = {
    val steps: Stream[Option[T]] = list.toStream.map { st =>
      Try(st(s)).toOption
    } takeWhile (_.isDefined)
    debug(s"Parsing: $s with ${ list.size } steps - ${ steps.mkString("[ ", " | ", " ]") }")
    steps.flatten.headOption
  }
}

case class ParserChain2[T](s: String)
  extends Logger {

  var methods = collection.mutable.ListBuffer[(String => T)]()

  def +(f: (String => T)): ParserChain2[T] = {
    methods += f
    this
  }

  def parse: Option[T] = {
    methods.toStream.map { st =>
      Try(st(s)).toOption
    }.find(_.isDefined).flatten
  }
}
