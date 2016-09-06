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

  abstract class EntityTableWithUUID(tag: Tag)
    extends EntityTable(tag) { def id: Rep[Option[UUID]] }

  override def table: TableQuery[_ <: EntityTableWithUUID]
  override def byIdentQuery(ident: UUID): Query[EntityTableWithUUID, IE, Seq] = table.filter(_.id === ident)

  override def insert(ie: IE): driver.api.DBIO[IE] = {
    super.insert(ie.withId(UUID.randomUUID()))
  }

  override def save(ie: IE): driver.api.DBIO[IE] = if (ie.id.isDefined) {
    super.save(ie)
  } else {
    insert(ie)
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
