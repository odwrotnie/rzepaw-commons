package slicky.entity

import slicky.Slicky._
import driver.api._

trait AnyIdentEntity
  extends AnyEntity {

  def meta: AnyIdentEntityMeta
  def save: DBIO[AnyIdentEntity]
  def update: DBIO[AnyIdentEntity]
  def delete: DBIO[AnyIdentEntity]
  def getOrInsert: DBIO[AnyIdentEntity]
  def updateOrInsert: DBIO[AnyIdentEntity]
  def readFromDB: DBIO[Option[AnyIdentEntity]]
}

trait AnyIdentEntityMeta
  extends AnyEntityMeta {

}
