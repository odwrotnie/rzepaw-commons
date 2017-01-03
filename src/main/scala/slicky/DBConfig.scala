package slicky

import javax.naming.InitialContext

import com.typesafe.scalalogging.LazyLogging
import commons.settings.{JNDI, Properties, SystemProperties}
import slick.driver.{H2Driver, JdbcDriver, JdbcProfile, MySQLDriver}
import com.typesafe.slick.driver.ms.SQLServerDriver
import slick.jdbc.JdbcBackend._

import scala.util.Try

abstract class DBConfig {

  val DB_URL = "slick" :: "db" :: "connection" :: "string" :: Nil
  val DB_USER = "slick" :: "db" :: "user" :: Nil
  val DB_PASS = "slick" :: "db" :: "password" :: Nil
  val DB_DRIVER = "slick" :: "db" :: "driver" :: Nil
  val JNDI_NAME = "default-data-source"
  val DEFAULT_CONNECTION = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1;MVCC=TRUE;MODE=MySQL"

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
  lazy val connectionString: Option[String] = Properties.get(DB_URL:_*)
  lazy val user: Option[String] = Properties.get(DB_USER:_*)
  lazy val password: Option[String] = Properties.get(DB_PASS:_*)
  lazy val driverClass: Option[String] = Properties.get(DB_DRIVER:_*)
  override def toString = super.toString + s": connection: $connectionString, driver: $driverClass, user: $user, password: $password"
}

object DefaultDBConfig
  extends SimpleDBConfig
    with LazyLogging {
  lazy val connectionString: Option[String] = Some(DEFAULT_CONNECTION)
  override def user: Option[String] = Some("sa")
  override def password: Option[String] = Some("")
  lazy val driverClass: Option[String] = Some("org.h2.Driver")
  // H2 Server
  org.h2.tools.Server.createTcpServer("-tcpAllowOthers").start()
}
