package commons.settings

import commons.logger.Logger

import scala.util.Try

/**
  * Lookup for the property value in system properties, then JNDI, then in resources
  */

object Properties
  extends Logger {

  lazy val CONFIG_RESOURCE_KEY = "config.resource"
  lazy val DEFAULT_CONFIG_RESOURCE = "config"

  lazy val configResource: String = System.getProperty(CONFIG_RESOURCE_KEY, DEFAULT_CONFIG_RESOURCE)

  def get(path: String*): Option[String] = {

    val configPath: List[String] = configResource :: path.toList

    val systemPropertiesConfigPath = configPath.mkString(".")
    val systemPropertiesPath = path.mkString(".")
    val jndiConfigPath = configPath.mkString(".")
    val jndiPath = path.mkString(".")
    val resourceConfigPath = s"/${ configResource + "." + path.head }.properties"
    val resourcePath = s"/${ path.head }.properties"
    val property = path.tail.mkString(".")

    val results: List[String] = List(
      SystemProperties.get(systemPropertiesConfigPath),
      SystemProperties.get(systemPropertiesPath),
      JNDI.get(jndiConfigPath),
      JNDI.get(jndiPath),
      ResourceProperties(resourceConfigPath).get(property),
      ResourceProperties(resourcePath).get(property)
    ).flatten

    val result = results.headOption
    debug(s"System properties lookup: $systemPropertiesConfigPath or $systemPropertiesPath, " +
      s"JNDI lookup: $jndiConfigPath or $jndiPath, " +
      s"resource properties lookup: $resourceConfigPath or $resourcePath " +
      s"= $result")

    result
  }

  def getInt(path: String*): Option[Int] = get(path:_*).flatMap(s => Try(s.toInt).toOption)
}
