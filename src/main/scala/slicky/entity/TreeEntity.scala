package slicky.entity

import slicky.Slicky._
import driver.api._

import scala.concurrent.Future

abstract class TreeEntity[TE <: TreeEntity[TE]](meta: TreeEntityMeta[TE])
  extends IdEntity[TE](meta) {
  self: TE =>
  def parentId: Option[ID]
  def parentId_=(i: Option[ID]): Unit

  def parent: Future[Option[TE]] =
    parentId.fold(Future.successful(Option.empty[TE]))(id => meta.byIdent(id))

  def children: Future[Seq[TE]] = dbFuture {
    meta.table.filter(_.parentId === id).result
  }

  def childrenCount: Future[Int] = dbFuture {
    meta.table.filter(_.parentId === id).length.result
  }

  def descendants: Future[Seq[TE]] = {
    val childrenChildren: Future[Seq[TE]] = children.flatMap { seq: Seq[TE] =>
      if (seq.isEmpty) {
        Future.successful(Seq[TE]())
      } else {
        Future.sequence(seq.map(_.descendants)).map(_.flatten)
      }
    }
    for {
      cc <- childrenChildren
      c <- children
    } yield cc ++ c
  }

  def descendantsCount: Future[Int] = {
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

  def path: Future[Seq[TE]] = for {
    op: Option[TE] <- parent
    seq: Seq[TE] <- op.map(_.path).getOrElse(Future.successful(Seq[TE]()))
  } yield op.toSeq ++ seq

  def pathString: String = path.await.reverse.mkString(" / ")

  def level: Future[Int] = parent.flatMap { op: Option[TE] =>
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

  def roots: Future[Seq[TE]] = dbFuture {
    table.filter(_.parentId isEmpty).result
  }
}
