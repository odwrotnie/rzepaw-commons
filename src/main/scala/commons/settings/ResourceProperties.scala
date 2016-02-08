package commons.settings

import java.util.Properties

import commons.logger.Logger

import scala.util.Try

case class ResourceProperties(path: String)
  extends Logger {

  require(path.startsWith("/"), "Path should start with forward slash")

  private val props: Option[Properties] = Try {
    val p = new Properties()
    p.load(getClass.getResourceAsStream(path))
    p
  }.toOption

  def get(prop: String): Option[String] = props flatMap { p =>
    val res = Try(p.getProperty(prop)).toOption.flatMap(Option(_))
    debug(s"Properties $path lookup: $prop - $res")
    res
  }
}
