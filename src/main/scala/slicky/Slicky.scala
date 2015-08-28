package slicky

import java.sql.Timestamp
import org.joda.time.DateTime
import slick.backend.DatabasePublisher
import slick.driver.H2Driver
import scala.concurrent._
import scala.concurrent.duration._

object Slicky {

  type ID = Long
  type IDOPT = Option[ID]

  implicit lazy val futureEC = scala.concurrent.ExecutionContext.Implicits.global

  val driver = H2Driver

  import driver.api._

  val DURATION = Duration.Inf
  val CONNECTION_STRING = "jdbc:h2:mem:wext-slick;DB_CLOSE_DELAY=-1;MVCC=TRUE"
  // val CONNECTION_STRING = "jdbc:h2:~/tmp/wext-slick.db"
  val DRIVER = "org.h2.Driver"
  val db = Database.forURL(CONNECTION_STRING, driver = DRIVER)

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
}
