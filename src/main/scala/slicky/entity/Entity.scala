package slicky.entity

import slicky.Slicky._
import driver.api._
import scala.concurrent.Future

abstract class Entity[E <: Entity[E]](meta: EntityMeta[E]) {
  self: E =>
  def insert = meta.insert(this)
}

abstract class EntityMeta[E <: Entity[E]] {

  type T = Table[E]
  def table: TableQuery[_ <: T]

  def insert(e: E): Future[E] = dbFuture {
    val newE = beforeInsert(e)
    (table += newE).named(s"Insert $e") map(i => newE)
  } map { e =>
    afterInsert(e)
  }

  def stream: Stream[E] = streamify(table)

  // BEFORE
  def beforeInsert(e: E): E = e

  // AFTER
  def afterInsert(e: E): E = e
}
