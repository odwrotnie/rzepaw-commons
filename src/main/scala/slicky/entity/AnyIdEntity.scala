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

  def byIdent(id: Long): DBIO[Option[AnyIdEntity]]
  def byIdentGet(id: Long): DBIO[AnyIdEntity]
  def byIdent(id: Option[Long]): DBIO[Option[AnyIdEntity]]
  def byIdentGet(id: Option[Long]): DBIO[Option[AnyIdEntity]]
}
