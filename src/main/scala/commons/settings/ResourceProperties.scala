package commons.settings

import java.util.Properties

import commons.logger.Logger

import scala.util.Try

case class ResourceProperties(path: String*)
  extends Logger {

  lazy val CONFIG_RESOURCE_KEY = "config.resource"
  lazy val DEFAULT_CONFIG_RESOURCE = "config"

  lazy val configResource = System.getProperty(CONFIG_RESOURCE_KEY, DEFAULT_CONFIG_RESOURCE)
  lazy val configPathString = (configResource + path).mkString("/", "/", "")
  lazy val defaultPathString = path.mkString("/", "/", "")

  /**
    * Load configPath properties or defaultPath
    */
  private val props: Option[Properties] = (configPathString :: defaultPathString :: Nil)
    .flatMap { pathString =>
      Try {
        val p = new Properties()
        p.load(getClass.getResourceAsStream(configPathString))
        p
      }.toOption
    }.headOption

  def get(prop: String): Option[String] = props flatMap { p =>
    val res = Try(p.getProperty(prop)).toOption.flatMap(Option(_))
    debug(s"Properties $configPathString lookup: $prop - $res")
    res
  }
}
