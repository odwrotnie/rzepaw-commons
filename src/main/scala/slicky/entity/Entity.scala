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
  def deleteAll(): Future[Int] = delete(allQuery)
  def delete(query: Query[T, E, Seq]): Future[Int] = dbFuture(query.delete)

  def insert(e: E): Future[E] = dbFuture {
    val newE = beforeInsert(e)
    (table += newE).named(s"Insert $e") map(i => newE)
  } map { e =>
    afterInsert(e)
    e
  }

  def allQuery: Query[T, E, Seq] = table
  def stream(query: Query[T, E, Seq]): Stream[E] = streamify(query)
  def stream: Stream[E] = stream(allQuery)
  def pages(pageSize: Int): Future[Long] = pages(allQuery, pageSize)
  def page(pageNum: Int, pageSize: Int): Future[Seq[E]] = page(allQuery, pageNum, pageSize)
  def page(query: Query[T, E, Seq], pageNum: Int, pageSize: Int): Future[Seq[E]] = {
    pages(query, pageSize) flatMap { pageCount: Long =>
      require(pageSize > 0)
      dbFuture {
        query.drop(pageNum * pageSize).take(pageSize).take(pageSize).result
      }
    }
  }
  def pages(query: Query[T, E, Seq], pageSize: Int): Future[Long] = dbFuture {
    query.length.result
  } map { length: Int =>
    Math.round(Math.ceil(length.toFloat / pageSize))
  }

  // BEFORE
  def beforeInsert(e: E): E = e

  // AFTER
  def afterInsert(e: E): Unit = {
    debug(s"Inserted (${ getClass.getSimpleName.replace("$", "") }): $e")
  }
}
