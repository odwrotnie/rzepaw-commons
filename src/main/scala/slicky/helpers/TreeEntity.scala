package slicky.helpers

import slicky.Slicky._
import slicky.Slicky.driver.api._
import slicky.entity._

import scala.concurrent.Future

abstract class TreeEntity[TE <: TreeEntity[TE]](meta: TreeEntityMeta[TE])
  extends IdEntity[TE](meta) {
  self: TE =>
  def parentId: Option[ID]
  def parentId_=(i: Option[ID]): Unit

  def parent: DBIO[Option[TE]] = parentId.fold(DBIO.successful(Option.empty[TE]))(id => meta.byIdent(id))

  def children: DBIO[Seq[TE]] = DBIO.sequence(meta.table.filter(_.parentId === id).result)

  def childrenCount: DBIO[Int] = meta.table.filter(_.parentId === id).length.result

  def descendants: DBIO[Seq[TE]] = {
    val childrenChildren: DBIO[Seq[TE]] = children.flatMap { seq: Seq[TE] =>
      if (seq.isEmpty) {
        DBIO.successful(Seq[TE]())
      } else {
        DBIO.sequence(seq.map(_.descendants)).map(_.flatten)
      }
    }
    for {
      cc <- childrenChildren
      c <- children
    } yield cc ++ c
  }

  def descendantsCount: DBIO[Int] = {
    val childrenChildrenCount: Future[Int] = children.flatMap { seq: Seq[TE] =>
      seq.foldLeft(Future.successful(0)) {
        (sumF, childrenF) => for {
          sum <- sumF
          children <- childrenF.descendantsCount
        } yield sum + children
      }
    }
    for {
      ccc <- childrenChildrenCount
      cc <- childrenCount
    } yield ccc + cc
  }

  def path: DBIO[Seq[TE]] = for {
    op: Option[TE] <- parent
    seq: Seq[TE] <- op.map(_.path).getOrElse(Future.successful(Seq[TE]()))
  } yield seq ++ op.toSeq
  def pathWithThis: Future[Seq[TE]] = path.map(p => p :+ this)
  def pathString: String = path.await.mkString(" / ")
  def pathStringWithThis: String = pathWithThis.await.mkString(" / ")

  def level: DBIO[Int] = parent.flatMap { op: Option[TE] =>
    op match {
      case Some(tn) => tn.level.map(_ + 1)
      case _ => Future.successful(0)
    }
  }
}

abstract class TreeEntityMeta[TE <: TreeEntity[TE]]
  extends IdEntityMeta[TE] {
  type TET = Table[TE] { def parentId: Rep[Option[ID]]}
  override def table: TableQuery[_ <: IET with TET]

  def roots: DBIO[Seq[TE]] = table.filter(_.parentId isEmpty).result
}
