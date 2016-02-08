package slicky

import java.sql.Timestamp
import commons.logger.Logger
import commons.settings.ResourceProperties
import org.joda.time.DateTime
import slick.jdbc.JdbcBackend._
import slick.backend.DatabasePublisher
import slick.lifted.CanBeQueryCondition
import slicky.entity.{IdentEntity, Entity, IdEntity}
import slick.driver._

import scala.concurrent._
import scala.concurrent.duration._

object Slicky
  extends Logger {

  // val properties = ResourceProperties("/db.properties")

  type ID = Long
  type AnyEntity = Entity[_ <: Entity[_]]
  type AnyIdentEntity = IdentEntity[_, _ <: IdentEntity[_, _]]
  type AnyIdEntity = IdEntity[_ <: IdEntity[_]]

  implicit lazy val futureEC = scala.concurrent.ExecutionContext.Implicits.global

  val DURATION = Duration.Inf

  val dbConfig: DBConfig = List(SystemPropertiesDBConfig, JNDIDBConfig, PropertiesDBConfig, DefaultDBConfig)
      .find(_.databaseDriver.isDefined).head
  infoAsciiArt("DB Configured")
  info(s"DB Config: $dbConfig")

  val databaseDriver = dbConfig.databaseDriver.get
  val db = databaseDriver._1
  val driver = databaseDriver._2
  import driver.api._

  // Run in transaction
  @deprecated("Use .future")
  def dbFuture[R](f: => DBIO[R]): Future[R] = db.run(f)
  def dbFutureSeq[R](f: => Seq[DBIO[R]]): Future[Unit] = db.run(DBIO.seq(f:_*))

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

  implicit def futureToSuperFuture[T](f: Future[T]): SuperFuture[T] = new SuperFuture[T](f)
  class SuperFuture[T](under: Future[T]) {
    def await: T = Await.result(under, DURATION)
  }

  implicit def dbioToSuperDBIO[T](a: DBIO[T]): SuperDBIO[T] = new SuperDBIO[T](a)
  class SuperDBIO[T](under: DBIO[T]) {
    def future: Future[T] = dbFuture(under)
    @deprecated("Use .future.await instead")
    def await: T = Await.result(future, DURATION)
  }

  def page[E](query: Query[_, E, Seq], pageNum: Int, pageSize: Int): Future[Seq[E]] = dbFuture {
    query.drop(pageNum * pageSize).take(pageSize).result
  }
  def pages[E](query: Query[_, E, Seq], pageSize: Int): Future[Long] = dbFuture {
    query.length.result
  } map { length: Int =>
    Math.round(Math.ceil(length.toFloat / pageSize))
  }
  def streamify[E](query: Query[_, E, Seq], pageSize: Int = 128): Stream[E] = {
    Stream.from(0) map { pageNum =>
      page(query, pageNum, pageSize).await
    } takeWhile(_.nonEmpty) flatten
  }

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
    def filter[T, R: CanBeQueryCondition](list: List[T])(f: List[T] => X => R) = if (list.nonEmpty) {
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
