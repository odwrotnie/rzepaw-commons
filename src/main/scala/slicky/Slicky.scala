package slicky

import java.sql.Timestamp
import java.util.Properties
import commons.logger.Logger
import org.joda.time.DateTime
import slick.backend.DatabasePublisher
import slick.driver.{MySQLDriver, H2Driver}
import slick.lifted.CanBeQueryCondition
import scala.concurrent._
import scala.concurrent.duration._

object Properties {
  private val PROPS = "/db.properties"
  private val props = new Properties()
  props.load(getClass.getResourceAsStream(PROPS))
  def get(prop: String) = props.getProperty(prop) match {
    case null => None
    case s => Some(s)
  }
}

object Slicky
  extends Logger {

  type ID = Long
  @deprecated
  type IDOPT = Option[ID]

  implicit lazy val futureEC = scala.concurrent.ExecutionContext.Implicits.global

  val DURATION = Duration.Inf
  lazy val CONNECTION_STRING: Option[String] = Properties.get("slick.db.connection.string")
  lazy val DRIVER_CLASS: Option[String] = Properties.get("slick.db.driver")
  lazy val USER: Option[String] = Properties.get("slick.db.user")
  lazy val PASSWORD: Option[String] = Properties.get("slick.db.password")
  lazy val driver = DRIVER_CLASS.get match {
    case "org.h2.Driver" => H2Driver
    case "com.mysql.jdbc.Driver" => MySQLDriver
  }

  import driver.api._

  lazy val db = (CONNECTION_STRING, DRIVER_CLASS, USER, PASSWORD) match {
    case (Some(c), Some(d), Some(u), Some(p)) =>
      Database.forURL(c, driver = d, user = u, password = p)
    case (Some(c), Some(d), _, _) =>
      Database.forURL(c, driver = d)
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
