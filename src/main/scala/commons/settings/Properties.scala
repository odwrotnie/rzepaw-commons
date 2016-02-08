package commons.settings

import commons.logger.Logger

import scala.util.Try

/**
  * Lookup for the property value in JNDI, than in system properties, than in resources
  */

object Properties
  extends Logger {

  def get(path: String*): Option[String] = {
    val results: List[String] = List(
      JNDI.get(path.mkString("/")),
      SystemProperties.get(path.mkString(("."))),
      ResourceProperties(s"/${ path.head }.properties").get(path.tail.mkString("."))
    ).flatten
    val result = results.headOption
    debug(s"JNDI or properties lookup: ${ path.mkString("/") } = $result")
    result
  }

  def getInt(path: String*): Option[Int] = get(path:_*).flatMap(s => Try(s.toInt).toOption)
}
