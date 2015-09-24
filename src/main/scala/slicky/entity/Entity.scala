package slicky.entity

import slicky.Slicky._
import driver.api._
import scala.concurrent.Future

abstract class Entity[E <: Entity[E]](val meta: EntityMeta[E]) {
  self: E =>
  def insert = meta.insert(this)
}

abstract class EntityMeta[E <: Entity[E]] {

  type T = Table[E]
  def table: TableQuery[_ <: T]

  def count: Int = dbAwait(table.size.result)

  def insert(e: E): Future[E] = dbFuture {
    val newE = beforeInsert(e)
    (table += newE).named(s"Insert $e") map(i => newE)
  } map { e =>
    afterInsert(e)
    e
  }

  def stream(query: Query[T, E, Seq]): Stream[E] = streamify(query)
  def stream: Stream[E] = stream(table)
  private def streamPage(stream: Stream[E], page: Int, pageSize: Int): Stream[E] =
    // stream.take((page + 1) * pageSize).takeRight(pageSize)
    stream.take((page + 1) * pageSize).reverse.take(pageSize).reverse
  def streamPage(query: Query[T, E, Seq], page: Int, pageSize: Int): Stream[E] =
    streamPage(stream(query), page, pageSize)
  def streamPage(page: Int, pageSize: Int): Stream[E] =
    streamPage(stream, page, pageSize)

  // BEFORE
  def beforeInsert(e: E): E = e

  // AFTER
  def afterInsert(e: E): Unit = {
    debug(s"Inserted (${ getClass.getSimpleName.replace("$", "") }): $e")
  }
}
