package slicky.entity

import commons.logger._
import org.scalatest.FunSuite
import slicky.Slicky._
import driver.api._

/*
sbt "~rzepaw-commons/testOnly slicky.entity.IdentEntityTest"
 */

class IdentEntityTest
  extends FunSuite
    with Logger {

  XYName.table.schema.create.await

  test("Insert entity") {

    val xyn1 = XYName(1, Some(1), "one").insert.await
    assert(XYName.byIdent((1, Some(1))).await.get.name == "one")

    val xyn2 = XYName(2, Some(2), "two").save.await
    assert(XYName.byIdent((2, Some(2))).await.get.name == "two")

    val xyn3 = XYName(3, Some(3), "three").save.await
    assert(XYName.stream.toList.size == 3)

    XYName.stream.foreach { e =>
      println(" - " + e)
    }

    xyn1.name = "11"
    xyn1.save.await
    assert(XYName.byIdent((1, Some(1))).await.get.name == "11")
  }

  test("Get by ident or insert") {
    val xyn = XYName(123, Some(456), "123456").insert.await
    val count = XYName.count
    XYName.getByIdentOrInsert(xyn).await
    assert(XYName.count == count)
  }

  test("Get by optional ident or insert") {
    val xyn = XYName(123, None, "123None").insert.await
    val count = XYName.count
    XYName.getByIdentOrInsert(xyn).await
    assert(XYName.count == count)
  }
}

case class XYName(var x: Int, var y: Option[Int], var name: String)
  extends IdentEntity[(Int, Option[Int]), XYName](XYName) {
  def ident = (x, y)
}

object XYName
  extends IdentEntityMeta[(Int, Option[Int]), XYName] {

  val table = TableQuery[Tbl]

  class Tbl(tag: Tag) extends EntityTable(tag) {

    def x = column[Int]("X")
    def y = column[Option[Int]]("Y")
    def name = column[String]("NAME")

    def * = (x, y, name) <>
      ((XYName.apply _).tupled, XYName.unapply)
  }

  override def byIdentQuery(ident: (Int, Option[Int])) =
    MaybeFilter(table)
      .filter(_.x === ident._1)
      .filter(ident._2.isEmpty)(_.y.isEmpty)
      .filter(ident._2.nonEmpty)(_.y === ident._2)
      .query
}
