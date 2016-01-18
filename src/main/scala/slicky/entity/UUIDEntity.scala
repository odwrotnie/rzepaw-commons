package slicky.entity

import java.util.UUID

import commons.logger.Logger
import slicky.Slicky._
import driver.api._

import scala.collection.immutable.HashSet
import scala.concurrent.Future

abstract class UUIDEntity[IE <: UUIDEntity[IE]](override val meta: UUIDEntityMeta[IE])
  extends IdentEntity[UUID, IE](meta) {
  self: IE =>
  def id: Option[UUID]
  def withId(id: UUID): IE
  override def ident: UUID = id match {
    case Some(id) => id
    case _ => throw new Exception(s"$this has no id yet")
  }
}

abstract class UUIDEntityMeta[IE <: UUIDEntity[IE]](tableName: String)
  extends IdentEntityMeta[UUID, IE] {

  UUIDEntities.add(this)

  type IET = Table[IE] { def id: Rep[Option[UUID]] }
  override def table: TableQuery[_ <: IET]
  override def byIdentQuery(ident: UUID): Query[IET, IE, Seq] = table.filter(_.id === ident)

  override def insert(ie: IE): DBIO[IE] = {
    require(ie.id.isEmpty)
    val newIE = beforeInsert(ie)
    val withId: IE = newIE.withId(UUID.randomUUID())
    val insertDBIO: DBIO[Int] = table += withId
    insertDBIO.map { count: Int =>
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

object UUIDEntities
  extends Logger {
  val all: collection.mutable.Set[UUIDEntityMeta[_]] = collection.mutable.HashSet[UUIDEntityMeta[_]]()
  def add(meta: UUIDEntityMeta[_]): Unit = {
    debug(s"UUIDEntities.add - $meta")
    all += meta
  }
  def byIdent(id: UUID): Option[UUIDEntity[_]] = {
    // TODO Switch to Stream so it computes until its found
    all.flatMap { em: UUIDEntityMeta[_] =>
      // TODO Get rid of asInstanceOf
      em.byIdent(id).await.map(_.asInstanceOf[UUIDEntity[_]])
    } headOption
  }
}
