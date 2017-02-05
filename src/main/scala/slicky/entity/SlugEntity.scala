package slicky.entity

import slicky.Slicky._
import driver.api._
import slicky.fields.SLUG
import scala.reflect.runtime.universe._

abstract class SlugEntity[SE <: SlugEntity[SE]](override val meta: SlugEntityMeta[SE])
  extends IdentEntity[SLUG[SE], SE](meta)
    with AnySlugEntity {

  self: SE =>

  def slug: SLUG[SE]
  def withSlug(slug: SLUG[SE]): SE
  override def ident: SLUG[SE] = slug

  def getOrInsertBySlug: DBIO[SE] = meta.getOrInsertBySlug(this)
  override def getOrInsert(query: Query[_, SE, Seq]): DBIO[SE] = meta.getOrInsert(query, this)
  override def updateOrInsert(query: Query[_, SE, Seq]): DBIO[SE] = meta.updateOrInsert(query, this)
}

abstract class SlugEntityMeta[SE <: SlugEntity[SE]](implicit tag: TypeTag[SE])
  extends IdentEntityMeta[SLUG[SE], SE]
    with AnySlugEntityMeta {

  abstract class EntityTableWithSlug(tag: Tag) extends EntityTable(tag) { def slug: Rep[SLUG[SE]] }

  override def table: TableQuery[_ <: EntityTableWithSlug]
  override def byIdentQuery(ident: SLUG[SE]): Query[EntityTableWithSlug, SE, Seq] = table.filter(_.slug === ident)

  override def insert(ie: SE): DBIO[SE] = {
    val newSE = beforeInsert(ie)
    val withSlug = ie.slug.value match {
      case s if s.isEmpty => newSE.withSlug(SLUG.generate[SE])
      case s => newSE
    }
    val idAction = table += withSlug
    idAction.map { _ =>
      afterInsert(withSlug)
      withSlug
    }
  }

  override def save(se: SE): DBIO[SE] =
    byIdent(se.ident).flatMap {
    case Some(se) =>
      val newSE = beforeSave(se)
      update(newSE).map { se =>
        afterSave(se)
        se
      }
    case None =>
      val newSE = beforeSave(se)
      insert(newSE).map { se =>
        afterSave(se)
        se
      }
  }

  def bySlug(slug: SLUG[SE]): DBIO[Option[SE]] = super.byIdent(slug)
  def bySlugGet(slug: SLUG[SE]): DBIO[SE] = super.byIdentGet(slug)
  def bySlug(slug: Option[SLUG[SE]]): DBIO[Option[SE]] = super.byIdent(slug)
  def bySlugGet(slug: Option[SLUG[SE]]): DBIO[Option[SE]] = super.byIdentGet(slug)

  def byIdentString(slug: String): DBIO[Option[SE]] = super.byIdent(SLUG[SE](slug))
  def byIdentStringGet(slug: String): DBIO[SE] = super.byIdentGet(SLUG[SE](slug))
  def byIdentString(slug: Option[String]): DBIO[Option[SE]] = super.byIdent(slug.map(s => SLUG[SE](s)))
  def byIdentStringGet(slug: Option[String]): DBIO[Option[SE]] = super.byIdentGet(slug.map(s => SLUG[SE](s)))

  //  private def cleanId(e: SE): SE = {
  //    val clone = e
  //    clone.getClass.getMethods.find(_.getName == "id_$eq").get.invoke(clone, None)
  //    clone
  //  }

  def getOrInsertBySlug(e: SE): DBIO[SE] = getByIdentOrInsert(e)

  override def getOrInsert(query: Query[_, SE, Seq], e: SE): DBIO[SE] = {
    query.length.result.flatMap {
      case i if i == 0 => insert(e.withSlug(SLUG[SE]("")))
      case i if i == 1 => super.getOrInsert(query, e)
    }
  }

  override def updateOrInsert(query: Query[_, SE, Seq], e: SE): DBIO[SE] = {
    query.length.result.flatMap {
      case i if i == 0 => insert(e.withSlug(SLUG[SE]("")))
      case i if i == 1 => query.result.head flatMap { fromDb => update(e.withSlug(fromDb.slug)) }
      case i => super.updateOrInsert(query, e)
    }
  }
}
