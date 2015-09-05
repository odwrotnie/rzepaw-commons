package slicky.entity

import commons.logger._
import org.scalatest.FunSuite
import slicky.Slicky._
import driver.api._

// sbt "~rzepawCommons/testOnly slicky.entity.EntityTest"
class IdentEntityTest
  extends FunSuite
  with Logger {

  dbAwait {
    XYName.table.schema.create
  }

  test("Insert entity") {

    val xyn1 = XYName(1, 1, "one").insert.await
    assert(XYName.byIdent((1, 1)).await.get.name == "one")

    val xyn2 = XYName(2, 2, "two").save.await
    assert(XYName.byIdent((1, 1)).await.get.name == "two")

    val xyn3 = XYName(3, 3, "three").save.await
    assert(XYName.stream.toList.size == 3)

    XYName.stream.foreach { e =>
      println(" - " + e)
    }

    xyn1.name = "11"
    xyn1.save.await
    assert(XYName.byIdent((1, 1)).await.get.name == "11")
  }
}

case class XYName(var x: Int, var y: Int, var name: String)
  extends IdentEntity[(Int, Int), XYName](XYName) {
  def ident = (x, y)
}

object XYName
  extends IdentEntityMeta[(Int, Int), XYName] {

  val table = TableQuery[Tbl]

  class Tbl(tag: Tag)
    extends Table[XYName](tag, "NAME") {

    def x = column[Int]("X")
    def y = column[Int]("Y")
    def name = column[String]("NAME")

    def * = (x, y, name) <>
      ((XYName.apply _).tupled, XYName.unapply)
  }

  override def byIdentQuery(ident: (Int, Int)) =
    table.filter(_.x === ident._1).filter(_.y === ident._2)
}
