package commons.settings

import javax.naming.InitialContext

import commons.logger.Logger

import scala.util.Try

object JNDI
  extends Logger {

  def ic = new InitialContext()

  def get(path: String): Option[String] = {
    val res = Try(ic.lookup(path).asInstanceOf[String]).toOption
    debug(s"JNDI lookup: $path - $res")
    res
  }
}
