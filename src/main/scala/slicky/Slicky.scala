package slicky

import java.sql.Timestamp

import com.typesafe.config.{Config, ConfigFactory}
import commons.logger.Logger
import commons.settings.ResourceProperties
import org.joda.time.DateTime
import rzepaw.configuration.Configuration
import slick.jdbc.JdbcBackend._
import slick.backend.DatabasePublisher
import slick.basic.DatabaseConfig
import slick.lifted.CanBeQueryCondition
import slicky.entity._
import slick.driver._
import slick.jdbc.{H2Profile, JdbcProfile}
import slicky.fields.ID

import scala.concurrent._
import scala.concurrent.duration._
import scala.util.Try

object Slicky
  extends Logger
    with Configuration {

  type RAW_ID = Long

  implicit lazy val futureEC = scala.concurrent.ExecutionContext.Implicits.global

  val DURATION = 60 seconds //Duration.Inf

  //  def CONFIG_ROOT = "model"
  //  val rootConfig = ConfigFactory.load
  //  val mode: String = rootConfig.getString("configuration.mode")
  //  val config = rootConfig.getConfig(mode)
  //
  //  println("Config       : " + config.getString("model.db.dataSourceClass"))
  //  println("Configuration: " + configuration.getString("model.db.dataSourceClass"))
  //
  //  1/0

  // TODO Dodac konfiguracje serwera jak jest mem
  //  /**
  //    * URL: jdbc:h2:tcp://155.133.45.93/mem:db
  //    */
  //  object DefaultDBConfig
  //    extends SimpleDBConfig
  //      with LazyLogging {
  //    lazy val connectionString: Option[String] = Some(DEFAULT_CONNECTION)
  //    override def user: Option[String] = Some("sa")
  //    override def password: Option[String] = Some("")
  //    lazy val driverClass: Option[String] = Some("org.h2.Driver")
  //    // H2 Server
  //    org.h2.tools.Server.createTcpServer("-tcpAllowOthers").start()
  //  }

  lazy val dbConfig: DatabaseConfig[JdbcProfile] =
    DatabaseConfig.forConfig[JdbcProfile]("model", configuration)
  lazy val profile: JdbcProfile = dbConfig.profile

  if (profile.isInstanceOf[H2Profile]) {
    org.h2.tools.Server.createTcpServer("-tcpAllowOthers").start()
  }

  infoAsciiArt("DB Configured")
  info(s"DB Config: $dbConfig")

  lazy val db: profile.api.Database = dbConfig.db
  val driver = profile
  import driver.api._

  // Wait for the result
  def await[R](f: => Future[R]): R = Await.result(f, DURATION)
  def await[R](futures: Seq[Future[R]]): Seq[R] = {
    var incompleteFutures = futures
    do {
      incompleteFutures = incompleteFutures.filterNot(_.isCompleted)
    } while (!incompleteFutures.isEmpty)
    futures.map(f => Await.result(f, DURATION))
  }

  // Stream the result
  def dbStream[R](f: => StreamingDBIO[_, R]): DatabasePublisher[R] = db.stream(f)

  implicit val DateTimeMapper = MappedColumnType.base[DateTime, Timestamp](
    (dt: DateTime) => new Timestamp(dt.getMillis),
    (t: Timestamp) => new DateTime(t.getTime)
  )

  implicit def futureToSuperFuture[T](f: Future[T]): SuperFuture[T] = new SuperFuture[T](f)
  class SuperFuture[T](under: Future[T]) {
    def await: T = Await.result({
      val f = under
      f.onFailure {
        case t =>
          error(s"Await error: ${ t.getMessage }")
          t.printStackTrace()
      }
      f
    }, DURATION)
    def awaitSafe: Option[T] = Try(await).toOption
  }

  implicit def dbioToSuperDBIO[T](a: DBIO[T]): SuperDBIO[T] = new SuperDBIO[T](a)
  class SuperDBIO[T](under: DBIO[T]) {
    def future: Future[T] = db.run(under)
    def futureTransactionally: Future[T] = db.run(under.transactionally)
    def await: T = futureToSuperFuture(future).await
    def awaitSafe: Option[T] = futureToSuperFuture(future).awaitSafe
    def awaitTransactionally: T = futureToSuperFuture(futureTransactionally).await
    def awaitTransactionallySafe: Option[T] = futureToSuperFuture(futureTransactionally).awaitSafe
  }

  def page[E](query: Query[_, E, Seq], pageNum: Long, pageSize: Int = 10): Future[Seq[E]] =
    query.drop(pageNum * pageSize).take(pageSize).result.future

  def pages[E](query: Query[_, E, Seq], pageSize: Long = 10): Future[Long] = query.length.result
    .map { length: Int =>
      Math.round(Math.ceil(length.toFloat / pageSize))
    } future

  def streamify[E](query: Query[_, E, Seq], pageSize: Int = 128): Stream[E] =
    Stream.from(0) map { pageNum =>
      page(query, pageNum, pageSize).await
    } takeWhile(_.nonEmpty) flatten

  /**
    * Optionally filters on a column with a supplied predicate
    *
    * @param query
    * @tparam X
    * @tparam Y
    */
  case class MaybeFilter[X, Y](query: Query[X, Y, Seq]) {
    def filter[T, R: CanBeQueryCondition](data: Option[T])(f: T => X => R) = {
      data.map(v => MaybeFilter(query.withFilter(f(v)))).getOrElse(this)
    }
    def filter[T, R: CanBeQueryCondition](list: Iterable[T])(f: Iterable[T] => X => R) = if (list.nonEmpty) {
      MaybeFilter(query.withFilter(f(list)))
    } else {
      this
    }
    def filter[R: CanBeQueryCondition](condition: Boolean)(f: X => R) = if (condition) {
      MaybeFilter(query.withFilter(f))
    } else {
      this
    }
    def filter[R: CanBeQueryCondition](f: X => R) =
      MaybeFilter(query.withFilter(f))
  }

  import org.reactivestreams.Publisher
  import rx.RxReactiveStreams
  implicit class PublisherToRxObservable[T](publisher: Publisher[T]) {
    def toObservable = RxReactiveStreams.toObservable(publisher)
  }

  def likeQueryString(s: String) = s"%${ s.toLowerCase }%"
}
