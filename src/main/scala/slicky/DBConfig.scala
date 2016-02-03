package slicky

import javax.naming.InitialContext

import commons.files.ResourceProperties
import slick.driver.{JdbcProfile, MySQLDriver, H2Driver, JdbcDriver}
import slick.jdbc.JdbcBackend._
import scala.util.Try

abstract class DBConfig {
  def dbDriver: Option[(DatabaseDef, JdbcProfile)] = for {
    db <- database
    dr <- driver
  } yield (db, dr)
  def driverClass: Option[String]
  lazy val driver: Option[JdbcProfile] = driverClass map {
    case "org.h2.Driver" => H2Driver
    case "com.mysql.jdbc.Driver" => MySQLDriver
  }
  def database: Option[DatabaseDef]
}

object JNDIDBConfig
  extends DBConfig {
  lazy val driverClass: Option[String] = Try {
    val ic = new InitialContext()
    ic.lookup("java:comp/env/databaseClassDriver").asInstanceOf[String]
  } toOption
  lazy val database = driver map { driver =>
    Database.forName("???")
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
  lazy val properties = ResourceProperties("/db.properties")
  lazy val connectionString: Option[String] = properties.get("slick.db.connection.string")
  lazy val user: Option[String] = properties.get("slick.db.user")
  lazy val password: Option[String] = properties.get("slick.db.password")
  lazy val driverClass: Option[String] = properties.get("slick.db.driver")
  override def toString = s"Database setup - connection: $connectionString, driver: $driverClass, user: $user, password: $password"
}

object DefaultDBConfig
  extends SimpleDBConfig {
  lazy val connectionString: Option[String] = Some("jdbc:h2:mem:wext-slick;DB_CLOSE_DELAY=-1;MVCC=TRUE")
  override def user: Option[String] = None
  override def password: Option[String] = None
  lazy val driverClass: Option[String] = Some("org.h2.Driver")
}
