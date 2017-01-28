package slicky.entity

import slicky.Slicky
import slicky.Slicky._
import driver.api._
import slick.sql.SqlProfile.ColumnOption.SqlType

import scala.concurrent.Future

abstract class Entity[E <: Entity[E]](val meta: EntityMeta[E])
  extends AnyEntity {

  self: E =>

  def insert: DBIO[E] = meta.insert(this)
  def getOrInsert(query: Query[_, E, Seq]): DBIO[E] = meta.getOrInsert(query, this)
  def updateOrInsert(query: Query[_, E, Seq]): DBIO[E] = meta.updateOrInsert(query, this)
}

abstract class EntityMeta[E <: Entity[E]]
  extends AnyEntityMeta {

  abstract class EntityTable(tag: Tag) extends Table[E](tag, tableName) {
    val STRING_COLUMN = O.Length(255)
    val TEXT_COLUMN = SqlType("TEXT")
    val FILE_COLUMN_SIZE = SqlType("MEDIUMBLOB")
    val TIMESTAMP_COLUMN = SqlType("timestamp default now()")
    val DATETIME_COLUMN = SqlType("DATETIME")
    val DATE_COLUMN = SqlType("DATE")
  }

  class Tbl
  def table: TableQuery[_ <: EntityTable]

  def delete(query: Query[EntityTable, E, Seq]): DBIO[Int] = query.delete
  def deleteAll(): DBIO[Int] = delete(allQuery)

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

  def updateByQuery(query: Query[_, E, Seq], e: E): DBIO[Int] = query.update(e)
  def updateOrInsert(query: Query[_, E, Seq], e: E): DBIO[E] = query.length.result.flatMap {
    case i if i == 0 => insert(e)
    case i if i == 1 => updateByQuery(query, e).map(_ => e)
    case i =>
      val results: Seq[E] = query.result.await
      DBIO.failed(new Exception(s"Update ${ getClass.getSimpleName } or insert $e query returned more than 1 ($i) row: ${ results.mkString(", ") }"))
  }

  def allQuery: Query[EntityTable, E, Seq] = table
  def stream(query: Query[EntityTable, E, Seq]): Stream[E] = Slicky.streamify(query)
  def stream: Stream[E] = stream(allQuery)

  def page(pageNum: Int, pageSize: Int): Future[Seq[E]] = page(allQuery, pageNum, pageSize)
  def pages(pageSize: Int): Future[Long] = pages(allQuery, pageSize)

  def page(query: Query[EntityTable, E, Seq], pageNum: Int, pageSize: Int): Future[Seq[E]] = Slicky.page(query, pageNum, pageSize)
  def pages(query: Query[EntityTable, E, Seq], pageSize: Int): Future[Long] = Slicky.pages(query, pageSize)

  // BEFORE
  def beforeInsert(e: E): E = e

  // AFTER
  def afterInsert(e: E): Unit = {
    debug(s"Inserted (${ getClass.getSimpleName.replace("$", "") }): $e")
  }

  override def toString: String = tableName
}
