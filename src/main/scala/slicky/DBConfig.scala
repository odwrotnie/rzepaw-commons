package slicky

import javax.naming.InitialContext
import commons.settings.{SystemProperties, JNDI, ResourceProperties}
import slick.driver.{JdbcProfile, MySQLDriver, H2Driver, JdbcDriver}
import com.typesafe.slick.driver.ms.SQLServerDriver
import slick.jdbc.JdbcBackend._
import scala.util.Try

abstract class DBConfig {

  val DB_URL = "slick.db.connection.string"
  val DB_USER = "slick.db.user"
  val DB_PASS = "slick.db.password"
  val DB_DRIVER = "slick.db.driver"

  def databaseDriver: Option[(DatabaseDef, JdbcProfile)] = for {
    db <- database
    dr <- driver
  } yield (db, dr)
  def driverClass: Option[String]
  lazy val driver: Option[JdbcProfile] = driverClass map {
    case "h2" | "org.h2.Driver" => H2Driver
    case "mysql" | "com.mysql.jdbc.Driver" => MySQLDriver
    case "mssql" | "com.typesafe.slick.driver.ms.SQLServerDriver" => SQLServerDriver
    case d => throw new Exception(s"No such driver specified in DBConfig - $d")
  }

  def database: Option[DatabaseDef]

  override def toString = s"${ getClass.getSimpleName.toUpperCase }"
}

object JNDIDBConfig
  extends DBConfig {
  lazy val driverClass: Option[String] = JNDI.get("jdbc/driver")
  lazy val database = driver map { driver =>
    Database.forName("default-data-source")
  }
}

abstract class SimpleDBConfig
  extends DBConfig {
  def connectionString: Option[String]
  def user: Option[String]
  def password: Option[String]
  lazy val database = driver flatMap { driver =>
    (connectionString, driverClass, user, password) match {
      case (Some(cs), Some(dc), Some(u), Some(p)) =>
        Some(Database.forURL(url = cs, driver = dc, user = u, password = p))
      case (Some(cs), Some(dc), _, _) =>
        Some(Database.forURL(cs, dc))
      case _ =>
        None
    }
  }
}

object PropertiesDBConfig
  extends SimpleDBConfig {
  lazy val properties = ResourceProperties("/jdbc.properties")
  lazy val connectionString: Option[String] = properties.get(DB_URL)
  lazy val user: Option[String] = properties.get(DB_USER)
  lazy val password: Option[String] = properties.get(DB_PASS)
  lazy val driverClass: Option[String] = properties.get(DB_DRIVER)
  override def toString = super.toString + s": connection: $connectionString, driver: $driverClass, user: $user, password: $password"
}

object SystemPropertiesDBConfig
  extends SimpleDBConfig {
  lazy val connectionString: Option[String] = SystemProperties.get(DB_URL)
  lazy val user: Option[String] = SystemProperties.get(DB_USER)
  lazy val password: Option[String] = SystemProperties.get(DB_PASS)
  lazy val driverClass: Option[String] = SystemProperties.get(DB_DRIVER)
  override def toString = super.toString + s": connection: $connectionString, driver: $driverClass, user: $user, password: $password"
}

object DefaultDBConfig
  extends SimpleDBConfig {
  lazy val connectionString: Option[String] = Some("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1;MVCC=TRUE")
  override def user: Option[String] = Some("sa")
  override def password: Option[String] = Some("")
  lazy val driverClass: Option[String] = Some("org.h2.Driver")
}
