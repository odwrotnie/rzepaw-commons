package slicky.entity

import commons.text.Slugify
import slicky.Slicky
import slicky.Slicky._
import driver.api._
import scala.concurrent.Future

abstract class Entity[E <: Entity[E]](val meta: EntityMeta[E]) {
  self: E =>
  def insert: DBIO[E] = meta.insert(this)
  def getOrInsert(query: Query[_, E, Seq]): DBIO[E] = meta.getOrInsert(query, this)
  def updateOrInsert(query: Query[_, E, Seq]): DBIO[E] = meta.updateOrInsert(query, this)
}

abstract class EntityMeta[E <: Entity[E]] {

  val tableName: String = Slugify(getClass.getSimpleName, "_").toUpperCase
  abstract class EntityTable(tag: Tag) extends Table[E](tag, tableName)

  def table: TableQuery[_ <: EntityTable]

  def count: Int = dbAwait(table.size.result)
  def deleteAll(): DBIO[Int] = delete(allQuery)
  def delete(query: Query[EntityTable, E, Seq]): DBIO[Int] = query.delete

  def insert(e: E): DBIO[E] = {
    val newE = beforeInsert(e)
    (table += newE).named(s"Insert $e") map(i => newE)
  } map { e =>
    afterInsert(e)
    e
  }

  def getOrInsert(query: Query[_, E, Seq], e: E): DBIO[E] = query.length.result.flatMap {
    case i if i == 0 => insert(e)
    case i if i == 1 => query.result.head
    case i =>
      val results: Seq[E] = query.result.await
      DBIO.failed(new Exception(s"Get ${ getClass.getSimpleName } or insert $e query returned more than 1 ($i) row: ${ results.mkString(", ") }"))
  }

  def updateOrInsert(query: Query[_, E, Seq], e: E): DBIO[E] = query.length.result.flatMap {
    case i if i == 0 => insert(e)
    case i if i == 1 => query.update(e).map(_ => e)
    case i =>
      val results: Seq[E] = query.result.await
      DBIO.failed(new Exception(s"Update ${ getClass.getSimpleName } or insert $e query returned more than 1 ($i) row: ${ results.mkString(", ") }"))
  }

  def allQuery: Query[EntityTable, E, Seq] = table
  def stream(query: Query[EntityTable, E, Seq]): Stream[E] = Slicky.streamify(query)
  def stream: Stream[E] = stream(allQuery)
  def pages(pageSize: Int): Future[Long] = pages(allQuery, pageSize)
  def page(pageNum: Int, pageSize: Int): Future[Seq[E]] = page(allQuery, pageNum, pageSize)
  def page(query: Query[EntityTable, E, Seq], pageNum: Int, pageSize: Int): Future[Seq[E]] = Slicky.page(query, pageNum, pageSize)
  def pages(query: Query[EntityTable, E, Seq], pageSize: Int): Future[Long] = Slicky.pages(query, pageSize)

  // BEFORE
  def beforeInsert(e: E): E = e

  // AFTER
  def afterInsert(e: E): Unit = {
    debug(s"Inserted (${ getClass.getSimpleName.replace("$", "") }): $e")
  }
}
