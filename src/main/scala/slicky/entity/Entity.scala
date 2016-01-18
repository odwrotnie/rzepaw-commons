package slicky.entity

import slicky.Slicky
import slicky.Slicky._
import driver.api._
import scala.concurrent.Future

abstract class Entity[E <: Entity[E]](val meta: EntityMeta[E]) {
  self: E =>
  def insert: DBIO[E] = meta.insert(this)
}

abstract class EntityMeta[E <: Entity[E]] {

  val tableName: String = getClass.getSimpleName.toUpperCase
  abstract class EntityTable(tag: Tag) extends Table[E](tag, tableName)

  type T = Table[E] // TODO Change to type T = EntityTable
  def table: TableQuery[_ <: T]

  def count: Int = dbAwait(table.size.result)
  def deleteAll(): DBIO[Int] = delete(allQuery)
  def delete(query: Query[T, E, Seq]): DBIO[Int] = query.delete

  def insert(e: E): DBIO[E] = {
    val newE = beforeInsert(e)
    (table += newE).named(s"Insert $e") map(i => newE)
  } map { e =>
    afterInsert(e)
    e
  }

  def allQuery: Query[T, E, Seq] = table
  def stream(query: Query[T, E, Seq]): Stream[E] = Slicky.streamify(query)
  def stream: Stream[E] = stream(allQuery)
  def pages(pageSize: Int): Future[Long] = pages(allQuery, pageSize)
  def page(pageNum: Int, pageSize: Int): Future[Seq[E]] = page(allQuery, pageNum, pageSize)
  def page(query: Query[T, E, Seq], pageNum: Int, pageSize: Int): Future[Seq[E]] = Slicky.page(query, pageNum, pageSize)
  def pages(query: Query[T, E, Seq], pageSize: Int): Future[Long] = Slicky.pages(query, pageSize)

  // BEFORE
  def beforeInsert(e: E): E = e

  // AFTER
  def afterInsert(e: E): Unit = {
    debug(s"Inserted (${ getClass.getSimpleName.replace("$", "") }): $e")
  }
}
