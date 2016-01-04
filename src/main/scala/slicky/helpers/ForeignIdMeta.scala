package slicky.helpers

import slicky.Slicky._
import driver.api._
import slicky.entity.{EntityMeta, Entity}
import scala.concurrent.Future

trait ForeignIdEntityMeta[FIE <: Entity[FIE] {def foreignId: Option[String]; def withForeignId(foreignId: String): FIE}] {

  type FIT = Table[FIE] { def foreignId: Rep[Option[String]] }
  def table: TableQuery[_ <: FIT]

  def byForeignIdQuery(foreignId: String): Query[FIT, FIE, Seq] =
    table.filter(_.foreignId === foreignId)

  def byForeignId(foreignId: String): Stream[FIE] = streamify(byForeignIdQuery(foreignId))

  def updateByForeignId(entity: FIE): DBIO[Option[FIE]] = {
    require(entity.foreignId.nonEmpty)
    val foreignId = entity.foreignId.get
    byForeignIdQuery(foreignId).update(entity) map {
      case rows if rows == 0 => None
      case rows if rows == 1 => byForeignId(foreignId).headOption
    }
  }
  def updateByForeignId(foreignId: String, entity: FIE): DBIO[Option[FIE]] = {
    val e = entity.withForeignId(foreignId)
    updateByForeignId(e)
  }

  def insert(e: FIE): DBIO[FIE]
  def updateOrInsertByForeignId(entity: FIE): DBIO[FIE] = {
    updateByForeignId(entity) flatMap {
      case Some(e) => DBIO.successful(e)
      case None => insert(entity)
    }
  }
  def updateOrInsertByForeignId(foreignId: String, entity: FIE): DBIO[FIE] = {
    val e = entity.withForeignId(foreignId)
    updateOrInsertByForeignId(e)
  }
}
