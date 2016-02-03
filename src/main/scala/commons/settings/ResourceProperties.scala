package commons.settings

import java.util.Properties

import scala.util.Try

case class ResourceProperties(path: String) {

  require(path.startsWith("/"), "Path should start with forward slash")

  private val props: Option[Properties] = Try {
    val p = new Properties()
    p.load(getClass.getResourceAsStream(path))
    p
  }.toOption

  def get(prop: String): Option[String] = props flatMap { p =>
    Try(p.getProperty(prop)).toOption.flatMap(Option(_))
  }
}
