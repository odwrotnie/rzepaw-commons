package commons.settings

import scala.util.Try

object JNDIOrProperties {

  def get(path: String*): Option[String] = {
    val results: List[String] = List(
      JNDI.get(path.mkString("/")),
      ResourceProperties(s"/${ path.head }.properties").get(path.tail.mkString("."))
    ).flatten
    results.headOption
  }

  def getInt(path: String*): Option[Int] = get(path:_*).flatMap(s => Try(s.toInt).toOption)
}
