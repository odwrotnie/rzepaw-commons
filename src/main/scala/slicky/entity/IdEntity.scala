package slicky.entity

import slicky.Slicky._
import driver.api._

import scala.concurrent.Future

abstract class IdEntity[IE <: IdEntity[IE]](override val meta: IdEntityMeta[IE])
  extends IdentEntity[ID, IE](meta) {
  self: IE =>
  def id: Option[ID]
  def withId(id: ID): IE
  override def ident: ID = id.get
}

abstract class IdEntityMeta[IE <: IdEntity[IE]]
  extends IdentEntityMeta[ID, IE] {

  type IET = Table[IE] { def id: Rep[ID] }
  override def table: TableQuery[_ <: IET]
  override def byIdentQuery(ident: ID): Query[IET, IE, Seq] = table.filter(_.id === ident)

  override def insert(ie: IE): Future[IE] = dbFuture {
    require(ie.id.isEmpty)
    val newIE = beforeInsert(ie)
    val idAction = (table returning table.map(_.id)) += ie
    idAction.map { id: ID =>
      val withId = newIE.withId(id)
      afterInsert(withId)
      withId
    }
  }

  override def save(ie: IE): Future[IE] = {
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
