package commons.settings

import java.util.Properties

import commons.logger.Logger

import scala.util.Try

case class ResourceProperties(path: String*)
  extends Logger {

  lazy val CONFIG_RESOURCE_KEY = "config.resource"
  lazy val DEFAULT_CONFIG_RESOURCE = "config"

  lazy val configResource = System.getProperty(CONFIG_RESOURCE_KEY, DEFAULT_CONFIG_RESOURCE)
  lazy val pathString = (configResource + path).mkString("/", "/", "")

  private val props: Option[Properties] = Try {
    val p = new Properties()
    p.load(getClass.getResourceAsStream(pathString))
    p
  }.toOption

  def get(prop: String): Option[String] = props flatMap { p =>
    val res = Try(p.getProperty(prop)).toOption.flatMap(Option(_))
    debug(s"Properties $pathString lookup: $prop - $res")
    res
  }
}
