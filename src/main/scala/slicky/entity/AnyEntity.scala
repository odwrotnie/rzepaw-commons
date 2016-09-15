package slicky.entity

import commons.logger.Logger
import commons.text.Slugify
import slicky.Slicky._
import driver.api._

import scala.collection.mutable.Set
import scala.concurrent.Future

trait AnyEntity {

  def meta: AnyEntityMeta
  def insert: DBIO[AnyEntity]
}

trait AnyEntityMeta {

  type EntityTable <: Table[_ <: AnyEntity]

  TblEntityMetaMap.add(this)

  val tableName: String = Slugify(getClass.getSimpleName, "_").toUpperCase
  def table: TableQuery[_ <: EntityTable]

  def count: Int = table.size.result.await
  def stream: Stream[AnyEntity]

  def page(pageNum: Int, pageSize: Int): Future[Seq[AnyEntity]]
  def pages(pageSize: Int): Future[Long]
}

// TODO Refactor to AnyEntityMeta
object TblEntityMetaMap
  extends Logger {

  def slug(meta: AnyEntityMeta): String = meta.tableName

  def meta(table: String): AnyEntityMeta = map.getOrElse(table, throw new Exception(s"No Meta Entity: $table"))
  def metaId(table: String): AnyIdEntityMeta = mapId.getOrElse(table, throw new Exception(s"No Meta Entity: $table"))

  lazy val map: Map[String, AnyEntityMeta] = list.map(meta => meta.tableName -> meta).toMap
  lazy val mapId: Map[String, AnyIdEntityMeta] = listId.map(meta => meta.tableName -> meta).toMap

  lazy val listId: Set[AnyIdEntityMeta] = list.collect { case x: AnyIdEntityMeta => x }

  private var list: Set[AnyEntityMeta] = Set()
  private[entity] def add(aem: AnyEntityMeta): Unit = {
    list += aem
    info(s"Added $aem to the list: ${ list.mkString(", ") }")
  }
}
