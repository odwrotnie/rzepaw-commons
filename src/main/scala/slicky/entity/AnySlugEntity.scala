package slicky.entity

import slicky.fields.{AnyID, AnySLUG, ID}
import slicky.Slicky._
import driver.api._

trait AnySlugEntity
  extends AnyIdentEntity {

  def meta: AnySlugEntityMeta
  def slug: AnySLUG
  def slugString: String = slug.value
  def ident: AnySLUG
  def identString: String = ident.value

  def save: DBIO[AnySlugEntity]
  def update: DBIO[AnySlugEntity]
  def delete: DBIO[AnySlugEntity]
  def getOrInsert: DBIO[AnySlugEntity]
  def updateOrInsert: DBIO[AnySlugEntity]
  def readFromDB: DBIO[Option[AnySlugEntity]]
}

trait AnySlugEntityMeta
  extends AnyIdentEntityMeta {

  def byIdentString(slug: String): DBIO[Option[AnySlugEntity]]
  def byIdentStringGet(slug: String): DBIO[AnySlugEntity]
  def byIdentString(slug: Option[String]): DBIO[Option[AnySlugEntity]]
  def byIdentStringGet(slug: Option[String]): DBIO[Option[AnySlugEntity]]
}

object AnySlugEntity {

  def by(table: String, slug: String): DBIO[AnySlugEntity] = {
    val meta = TblEntityMetaMap.metaSlug(table)
    meta.byIdentStringGet(slug)
  }
}
