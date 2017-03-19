package slicky.helpers

import slicky.Slicky._
import slicky.Slicky.driver.api._
import slicky.entity._
import slicky.fields.ID
import scala.reflect.runtime.universe._

abstract class TreeEntity[TE <: TreeEntity[TE]](meta: TreeEntityMeta[TE])(implicit tag: TypeTag[TE])
  extends IdEntity[TE](meta) {

  self: TE =>

  def parentId: Option[ID[TE]]
  def parentId_=(id: Option[ID[TE]]): Unit

  def parent: DBIO[Option[TE]] = parentId match {
    case Some(id) => meta.byIdent(id)
    case _ => DBIO.successful(None)
  }
  def isRoot: DBIO[Boolean] = parent.map(_.isDefined)

  def children: DBIO[Set[TE]] = meta.table.filter(_.parentId === id).result.map(_.toSet)

  def childrenCount: DBIO[Int] = meta.table.filter(_.parentId === id).length.result

  def descendants: DBIO[Set[TE]] = {
    val childrenChildren: DBIO[Set[TE]] = children.flatMap { set: Set[TE] =>
      if (set.isEmpty) {
        DBIO.successful(Set())
      } else {
        DBIO.sequence(set.toSeq.map(_.descendants)).map(_.flatten.toSet)
      }
    }
    for {
      cc <- childrenChildren
      c <- children
    } yield cc ++ c
  }

  def descendantsCount: DBIO[Int] = {
    val childrenChildrenCount: DBIO[Int] = children.map { set: Set[TE] =>
      set.foldLeft(0)((sum, childrenF) => sum + childrenF.descendantsCount.await)
    }
    for {
      ccc <- childrenChildrenCount
      cc <- childrenCount
    } yield ccc + cc
  }

  def path: DBIO[Seq[TE]] = for {
    op: Option[TE] <- parent
    seq: Seq[TE] <- op.map(_.path).getOrElse(DBIO.successful(Seq[TE]()))
  } yield seq ++ op.toSeq
  def pathWithThis: DBIO[Seq[TE]] = path.map(p => p :+ this)
  def pathString: DBIO[String] = path.map(_.mkString(" / "))
  def pathStringWithThis: DBIO[String] = pathWithThis.map(_.mkString(" / "))

  def level: DBIO[Int] = parent.flatMap { op: Option[TE] =>
    op match {
      case Some(tn) => tn.level.map(_ + 1)
      case _ => DBIO.successful(0)
    }
  }
}

abstract class TreeEntityMeta[TE <: TreeEntity[TE]](implicit tag: TypeTag[TE])
  extends IdEntityMeta[TE] {
  abstract class EntityTableWithIdAndParent(tag: Tag)
    extends EntityTableWithId(tag) { def parentId: Rep[Option[ID[TE]]]}
  override def table: TableQuery[_ <: EntityTableWithIdAndParent]

  def roots: DBIO[Seq[TE]] = table.filter(_.parentId isEmpty).result
}
