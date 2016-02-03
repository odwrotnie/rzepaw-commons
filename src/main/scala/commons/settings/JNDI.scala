package commons.settings

import javax.naming.InitialContext

import commons.logger.Logger

import scala.util.Try

object JNDI
  extends Logger {

  def ic = new InitialContext()

  def get(path: String): Option[String] = {
    debug(s"JNDI lookup: $path")
    Try(ic.lookup(path).asInstanceOf[String]).toOption
  }
}
