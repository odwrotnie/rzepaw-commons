package commons.settings

import javax.naming.InitialContext

import scala.util.Try

object JNDI {

  def ic = new InitialContext()

  def get(path: String): Option[String] =
    Try(ic.lookup("gmc/topic").asInstanceOf[String]).toOption
}
