package commons.settings

import java.util.Properties

import commons.logger.Logger

import scala.util.Try

object SystemProperties
  extends Logger {

  def get(prop: String): Option[String] = {
    val res = Try(System.getProperty(prop)).toOption.flatMap(Option(_))
    debug(s"System properties lookup: $prop - $res")
    res
  }
}
