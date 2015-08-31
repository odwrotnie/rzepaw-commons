package slicky

import slicky.Slicky._
import driver.api._
import scala.concurrent.Future

abstract class TreeNode[TN <: Model[TN] with TreeNode[TN]](override val meta: TreeNodeMeta[TN])
  extends Model[TN](meta) {

  self: TN =>

  def parentId: Option[ID]
  def parentId_=(i: Option[ID]): Unit

  def parent: Future[Option[TN]] =
    parentId.fold(Future.successful(Option.empty[TN]))(id => meta.byId(id))

  def children: Future[Seq[TN]] = dbFuture {
    meta.table.filter(_.parentId === id).result
  }

  def childrenCount: Future[Int] = dbFuture {
    meta.table.filter(_.parentId === id).length.result
  }

  def descendants: Future[Seq[TN]] = {
    val childrenChildren: Future[Seq[TN]] = children.flatMap { seq: Seq[TN] =>
      if (seq.isEmpty) {
        Future.successful(Seq[TN]())
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
    val childrenChildrenCount: Future[Int] = children.flatMap { seq: Seq[TN] =>
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

  def path: Future[Seq[TN]] = for {
    op: Option[TN] <- parent
    seq: Seq[TN] <- op.map(_.path).getOrElse(Future.successful(Seq[TN]()))
  } yield op.toSeq ++ seq

  def pathString: String = path.await.reverse.mkString(" / ")

  def level: Future[Int] = parent.flatMap { op: Option[TN] =>
    op match {
      case Some(tn) => tn.level.map(_ + 1)
      case _ => Future.successful(0)
    }
  }
}

trait TreeNodeMeta[TN <: Model[TN] with TreeNode[TN]]
  extends MetaModel[TN] {

  type TNT = Table[TN] { def parentId: Rep[Option[ID]] }
  override def table: TableQuery[_ <: T with TNT]

  def roots: Future[Seq[TN]] = dbFuture {
    table.filter(_.parentId isEmpty).result
  }
}
