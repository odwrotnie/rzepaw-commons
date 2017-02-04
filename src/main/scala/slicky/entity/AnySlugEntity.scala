//package slicky.entity
//
//import slicky.fields.{AnyID, AnySLUG, ID}
//import slicky.Slicky._
//import driver.api._
//
//trait AnySlugEntity
//  extends AnyIdentEntity {
//
//  def meta: AnySlugEntityMeta
//  def id: Option[AnySLUG]
//  def idString: Option[String] = id.map(_.value)
//  def ident: AnySLUG
//  def identString: String = ident.value
//
//  def save: DBIO[AnyIdEntity]
//  def update: DBIO[AnyIdEntity]
//  def delete: DBIO[AnyIdEntity]
//  def getOrInsert: DBIO[AnyIdEntity]
//  def updateOrInsert: DBIO[AnyIdEntity]
//  def readFromDB: DBIO[Option[AnyIdEntity]]
//}
//
//trait AnySlugEntityMeta
//  extends AnyIdentEntityMeta {
//
//  def byIdentSlug(slug: String): DBIO[Option[AnyIdEntity]]
//  def byIdentSlugGet(slug: String): DBIO[AnyIdEntity]
//  def byIdentSlug(slug: Option[String]): DBIO[Option[AnyIdEntity]]
//  def byIdentSlugGet(slug: Option[String]): DBIO[Option[AnyIdEntity]]
//}
//
//object AnyIdEntity {
//
//  def by(table: String, slug: String): DBIO[AnyIdEntity] = {
//    val meta = TblEntityMetaMap.metaSlug(table)
//    meta.byIdentSlugGet(slug)
//  }
//}
