package slicky

import java.io.InputStream
import java.sql.Timestamp
import commons.logger.Logger
import org.joda.time.DateTime
import slick.backend.DatabasePublisher
import slick.driver.{MySQLDriver, H2Driver}
import slick.lifted.CanBeQueryCondition
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.Try

object Properties {
  import java.util.Properties
  private val PROPS = "/db.properties"
  private val props: Option[Properties] = Try {
    val p = new Properties()
    p.load(getClass.getResourceAsStream(PROPS))
    p
  }.toOption
  def get(prop: String): Option[String] = props flatMap { p =>
    Try(p.getProperty(prop)).toOption.flatMap(Option(_))
  }
}

object Slicky
  extends Logger {

  type ID = Long
  @deprecated
  type IDOPT = Option[ID]

  implicit lazy val futureEC = scala.concurrent.ExecutionContext.Implicits.global

  val DURATION = Duration.Inf
  lazy val CONNECTION_STRING: String = Properties.get("slick.db.connection.string").getOrElse("jdbc:h2:mem:wext-slick;DB_CLOSE_DELAY=-1;MVCC=TRUE")
  lazy val DRIVER_CLASS: String = Properties.get("slick.db.driver").getOrElse("org.h2.Driver")
  lazy val USER: Option[String] = Properties.get("slick.db.user")
  lazy val PASSWORD: Option[String] = Properties.get("slick.db.password")
  lazy val driver = DRIVER_CLASS match {
    case "org.h2.Driver" => H2Driver
    case "com.mysql.jdbc.Driver" => MySQLDriver
  }

  import driver.api._

  info(s"Database setup - connection: $CONNECTION_STRING, driver: $DRIVER_CLASS, user: $USER, password: $PASSWORD")

  lazy val db = (USER, PASSWORD) match {
    case (Some(u), Some(p)) =>
      Database.forURL(CONNECTION_STRING, driver = DRIVER_CLASS, user = u, password = p)
    case _ =>
      Database.forURL(CONNECTION_STRING, driver = DRIVER_CLASS)
  }

  // Run in transaction
  def dbFuture[R](f: => DBIO[R]): Future[R] = db.run(f)

  // Run in transaction and wait for the result
  def dbAwait[R](f: => DBIO[R]): R = await(dbFuture(f))

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

  implicit def futureToSuperFuture[T](f: Future[T]) = new SuperFuture[T](f)
  class SuperFuture[T](under: Future[T]) {
    def await: T = Await.result(under, DURATION)
  }

  def streamify[T](query: Query[_,T,Seq], pageSize: Int = 31): Stream[T] = {
    require(pageSize > 0)
    val length: Int = dbAwait { query.length.result }
    val pageCount: Long = Math.round(Math.ceil(length.toFloat / pageSize))
    Stream.from(0).takeWhile(_ < pageCount).flatMap { page =>
      dbAwait { query.drop(page * pageSize).take(pageSize).result }
    }
  }

  case class MaybeFilter[X, Y](val query: Query[X, Y, Seq]) {
    def filter[T, R: CanBeQueryCondition](data: Option[T])(f: T => X => R) = {
      data.map(v => MaybeFilter(query.withFilter(f(v)))).getOrElse(this)
    }
  }

  import org.reactivestreams.Publisher
  import rx.RxReactiveStreams
  implicit class PublisherToRxObservable[T](publisher: Publisher[T]) {
    def toObservable= RxReactiveStreams.toObservable(publisher)
  }
}
