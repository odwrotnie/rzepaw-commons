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

  def updateByForeignId(entity: FIE): DBIO[Int] = {
    require(entity.foreignId.nonEmpty)
    val foreignId = entity.foreignId.get
    byForeignIdQuery(foreignId).update(entity)
  }
  def updateByForeignId(foreignId: String, entity: FIE): DBIO[Int] = {
    val e = entity.withForeignId(foreignId)
    updateByForeignId(e)
  }

  def insert(e: FIE): DBIO[FIE]
  def updateOrInsertByForeignId(entity: FIE): DBIO[FIE] = {
    updateByForeignId(entity) flatMap {
      case rows if rows == 0 => insert(entity)
      case _ => DBIO.successful(entity)
    }
  }
  def updateOrInsertByForeignId(foreignId: String, entity: FIE): DBIO[FIE] = {
    val e = entity.withForeignId(foreignId)
    updateOrInsertByForeignId(e)
  }
}
