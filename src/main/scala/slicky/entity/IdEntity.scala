package slicky.entity

import slicky.Slicky._
import driver.api._

import scala.concurrent.Future

abstract class IdEntity[IE <: IdEntity[IE]](override val meta: IdEntityMeta[IE])
  extends IdentEntity[ID, IE](meta) {
  self: IE =>
  def id: Option[ID]
  def withId(id: ID): IE
  override def ident: ID = id match {
    case Some(id) => id
    case _ => throw new Exception(s"$this has no id yet")
  }
}

abstract class IdEntityMeta[IE <: IdEntity[IE]]
  extends IdentEntityMeta[ID, IE] {

  abstract class EntityTableWithId(tag: Tag)
    extends EntityTable(tag) { def id: Rep[ID] }
  override def table: TableQuery[_ <: EntityTableWithId]
  override def byIdentQuery(ident: ID): Query[EntityTableWithId, IE, Seq] = table.filter(_.id === ident)

  override def insert(ie: IE): DBIO[IE] = {
    require(ie.id.isEmpty, s"Inserting entity $ie with defined id: ${ ie.ident }")
    val newIE = beforeInsert(ie)
    val idAction = (table returning table.map(_.id)) += ie
    idAction.map { id: ID =>
      val withId = newIE.withId(id)
      afterInsert(withId)
      withId
    }
  }

  override def save(ie: IE): DBIO[IE] = {
    val newIE = beforeSave(ie)
    ie.id match {
      case Some(id) => update(ie.ident, newIE).map { _ =>
        afterSave(newIE)
        newIE
      }
      case _ => insert(newIE).map { withId =>
        afterSave(withId)
        withId
      }
    }
  }
}
