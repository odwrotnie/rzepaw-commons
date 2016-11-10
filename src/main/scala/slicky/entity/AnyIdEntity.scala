package slicky.entity

import slicky.fields.{AnyID, ID}
import slicky.Slicky._
import driver.api._

trait AnyIdEntity
  extends AnyIdentEntity {

  def meta: AnyIdEntityMeta
  def id: Option[AnyID]
  def idNumber: Option[Long] = id.map(_.value)
  def ident: AnyID
  def identNumber: Long = ident.value

  def save: DBIO[AnyIdEntity]
  def update: DBIO[AnyIdEntity]
  def delete: DBIO[AnyIdEntity]
  def getOrInsert: DBIO[AnyIdEntity]
  def updateOrInsert: DBIO[AnyIdEntity]
  def readFromDB: DBIO[Option[AnyIdEntity]]
}

trait AnyIdEntityMeta
  extends AnyIdentEntityMeta {

  def byIdentNumber(id: Long): DBIO[Option[AnyIdEntity]]
  def byIdentNumberGet(id: Long): DBIO[AnyIdEntity]
  def byIdentNumber(id: Option[Long]): DBIO[Option[AnyIdEntity]]
  def byIdentNumberGet(id: Option[Long]): DBIO[Option[AnyIdEntity]]
}

object AnyIdEntity {

  def by(table: String, id: Long): DBIO[AnyIdEntity] = {
    val meta = TblEntityMetaMap.metaId(table)
    meta.byIdentNumberGet(id)
  }
}
