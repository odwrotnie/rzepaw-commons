package slicky.entity

import slicky.Slicky._
import driver.api._
import slicky.fields.SLUG
import scala.reflect.runtime.universe._

abstract class SlugEntity[SE <: SlugEntity[SE]](override val meta: SlugEntityMeta[SE])
  extends IdentEntity[SLUG[SE], SE](meta)
    with AnySlugEntity {

  self: SE =>

  def id: Option[SLUG[SE]]
  def withId(id: Option[SLUG[SE]]): SE
  override def ident: SLUG[SE] = id match {
    case Some(id) => id
    case _ => throw new Exception(s"Entity $this has no id yet")
  }

  override def getOrInsert(query: Query[_, SE, Seq]): DBIO[SE] = meta.getOrInsert(query, this)
  override def updateOrInsert(query: Query[_, SE, Seq]): DBIO[SE] = meta.updateOrInsert(query, this)
}

abstract class SlugEntityMeta[SE <: SlugEntity[SE]](implicit tag: TypeTag[SE])
  extends IdentEntityMeta[SLUG[SE], SE]
    with AnySlugEntityMeta {

  abstract class EntityTableWithId(tag: Tag) extends EntityTable(tag) { def id: Rep[SLUG[SE]] }

  override def table: TableQuery[_ <: EntityTableWithId]
  override def byIdentQuery(ident: SLUG[SE]): Query[EntityTableWithId, SE, Seq] = table.filter(_.id === ident)

  override def insert(ie: SE): DBIO[SE] = {
    require(ie.id.isEmpty, s"Inserting entity $ie with defined id: ${ ie.ident }")
    val newSE = beforeInsert(ie)
    val idAction = (table returning table.map(_.id)) += ie
    idAction.map { id: SLUG[SE] =>
      val withId = newSE.withId(Some(id))
      afterInsert(withId)
      withId
    }
  }

  override def save(ie: SE): DBIO[SE] = {
    val newSE = beforeSave(ie)
    ie.id match {
      case Some(id) => update(ie.ident, newSE).map { _ =>
        afterSave(newSE)
        newSE
      }
      case _ => insert(newSE).map { withId =>
        afterSave(withId)
        withId
      }
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

  override def getOrInsert(query: Query[_, SE, Seq], e: SE): DBIO[SE] = {
    query.length.result.flatMap {
      case i if i == 0 => insert(e.withId(None))
      case i if i == 1 => super.getOrInsert(query, e)
    }
  }

  override def updateOrInsert(query: Query[_, SE, Seq], e: SE): DBIO[SE] = {
    query.length.result.flatMap {
      case i if i == 0 => insert(e.withId(None))
      case i if i == 1 => query.result.head flatMap { fromDb => update(e.withId(fromDb.id)) }
      case i => super.updateOrInsert(query, e)
    }
  }
}
