package slicky.entity

import slicky.fields.{AnyID, AnySLUG, ID}
import slicky.Slicky._
import driver.api._

trait AnySlugEntity
  extends AnyIdentEntity {

  def meta: AnySlugEntityMeta
  def id: Option[AnySLUG]
  def idString: Option[String] = id.map(_.value)
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

  def byIdentSlug(slug: String): DBIO[Option[AnySlugEntity]]
  def byIdentSlugGet(slug: String): DBIO[AnySlugEntity]
  def byIdentSlug(slug: Option[String]): DBIO[Option[AnySlugEntity]]
  def byIdentSlugGet(slug: Option[String]): DBIO[Option[AnySlugEntity]]
}

object AnySlugEntity {

  def by(table: String, slug: String): DBIO[AnySlugEntity] = {
    val meta = TblEntityMetaMap.metaSlug(table)
    meta.byIdentSlugGet(slug)
  }
}
