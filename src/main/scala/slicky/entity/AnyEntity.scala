package slicky.entity

import commons.logger.Logger
import commons.text.Slugify
import slick.jdbc.meta.MTable
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

  val tableName: String = Slugify(getClass.getSimpleName, "_").toUpperCase
  def table: TableQuery[_ <: EntityTable]

  def isEmpty: DBIO[Boolean] = table.size.result.map(_ <= 0)
  def count: DBIO[Int] = table.size.result
  def stream: Stream[AnyEntity]

  def page(pageNum: Int, pageSize: Int): Future[Seq[AnyEntity]]
  def pages(pageSize: Int): Future[Long]

  // TODO Create schema
//    if (MTable.getTables(tableName).await.nonEmpty) {
//      debug(s"Creating table $tableName")
//      table.schema.create
//    } else {
//      warn(s"Table $tableName already exists")
//    }
  // Add table to map
  TblEntityMetaMap.add(this)
}

// TODO Refactor to AnyEntityMeta
object TblEntityMetaMap
  extends Logger {

  def slug(meta: AnyEntityMeta): String = meta.tableName

  def meta(table: String): AnyEntityMeta = map.getOrElse(table, throw new Exception(s"No Meta Entity: $table"))
  def metaId(table: String): AnyIdEntityMeta = mapId.getOrElse(table, throw new Exception(s"No Meta Entity: $table"))
  def metaSlug(table: String): AnySlugEntityMeta = ??? //mapId.getOrElse(table, throw new Exception(s"No Meta Entity: $table"))

  def anyIdEntity(table: String, idNumber: Long): DBIO[AnyIdEntity] = metaId(table).byIdentNumberGet(idNumber)

  lazy val map: Map[String, AnyEntityMeta] = list.map(meta => meta.tableName -> meta).toMap
  lazy val mapId: Map[String, AnyIdEntityMeta] = listId.map(meta => meta.tableName -> meta).toMap

  lazy val listId: Set[AnyIdEntityMeta] = list.collect { case x: AnyIdEntityMeta => x }

  private var list: Set[AnyEntityMeta] = Set()
  private[entity] def add(aem: AnyEntityMeta): Unit = {
    list += aem
    info(s"Added $aem to the list")
  }
}
