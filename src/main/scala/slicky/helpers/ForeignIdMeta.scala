package slicky.helpers

import slicky.Slicky._
import driver.api._
import slicky.entity._

trait ForeignIdEntityMeta[FIE <: Entity[FIE] {def foreignId: Option[String]; def withForeignId(foreignId: String): FIE}] {

  type FIT = Table[FIE] { def foreignId: Rep[Option[String]] }
  def table: TableQuery[_ <: FIT]

  def byForeignIdQuery(foreignId: String): Query[FIT, FIE, Seq] =
    table.filter(_.foreignId === foreignId)

  def byForeignId(foreignId: String): Stream[FIE] = streamify(byForeignIdQuery(foreignId))
  def fromDb(entity: FIE): Stream[FIE] = {
    require(entity.foreignId.nonEmpty)
    byForeignId(entity.foreignId.get)
  }

  def updateByForeignId(e: FIE): DBIO[List[FIE]] = {
    require(e.foreignId.nonEmpty)
    val foreignId = e.foreignId.get
    val entities: List[DBIO[FIE]] = fromDb(e).toList map {
      case idEntity: IdEntity[_] => // Prevents overwriting auto-generated id with None (earlier it was given new id)
        val ie: FIE = e.asInstanceOf[IdEntity[_]].withId(idEntity.id).asInstanceOf[FIE]
        byForeignIdQuery(foreignId).update(ie).map(_ => ie)
      case entity =>
        byForeignIdQuery(foreignId).update(entity).map(_ => entity)
    }
    DBIO.sequence(entities)
  }
  def updateByForeignId(foreignId: String, entity: FIE): DBIO[_] = {
    val e = entity.withForeignId(foreignId)
    updateByForeignId(e)
  }

  def insert(e: FIE): DBIO[FIE]
  def updateOrInsertByForeignId(entity: FIE): DBIO[List[FIE]] = {
    updateByForeignId(entity) flatMap {
      case Nil => DBIO.sequence(insert(entity) :: Nil)
      case list => DBIO.successful(list)
    }
  }
  def updateOrInsertByForeignId(foreignId: String, entity: FIE): DBIO[_] = {
    val e = entity.withForeignId(foreignId)
    updateOrInsertByForeignId(e)
  }
}
