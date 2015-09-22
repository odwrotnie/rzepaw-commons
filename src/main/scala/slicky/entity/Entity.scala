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

  def query: Query[T, E, Seq] = table
  def stream(query: Query[T, E, Seq]): Stream[E] = streamify(query)
  def stream: Stream[E] = stream(query)

  // BEFORE
  def beforeInsert(e: E): E = e

  // AFTER
  def afterInsert(e: E): Unit = {
    debug(s"Inserted (${ getClass.getSimpleName.replace("$", "") }): $e")
  }
}
