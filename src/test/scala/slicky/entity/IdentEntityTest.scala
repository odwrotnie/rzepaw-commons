//package slicky.entity
//
//import commons.logger._
//import org.scalatest.FunSuite
//import slicky.Slicky._
//import driver.api._
//
//// sbt "~rzepawCommons/testOnly slicky.entity.EntityTest"
//class IdentEntityTest
//  extends FunSuite
//  with Logger {
//
//  dbAwait {
//    XYName.table.schema.create
//  }
//
//  test("Insert entity") {
//
//    val in1 = XYName("one", 1).insert.await
//    val in2 = XYName("two", 2).insert.await
//    val in3 = XYName("three", 3).insert.await
//    assert(XYName.stream.toList.size == 3)
//
//    XYName.stream.foreach { e =>
//      println(" - " + e)
//    }
//  }
//}
//
//case class XYName(var x: Int, var y: String, var name: String)
//  extends IdentEntity[(Int, Int), XYName](XYName) {
//  def ident = (x, y)
//}
//
//object XYName
//  extends IdentEntityMeta[(Int, Int), XYName] {
//
//  val table = TableQuery[Tbl]
//
//  class Tbl(tag: Tag)
//    extends Table[XYName](tag, "NAME") {
//
//    def x = column[String]("X")
//    def y = column[String]("Y")
//    def name = column[String]("NAME")
//
//    def * = (x, y, name) <>
//      ((XYName.apply _).tupled, XYName.unapply)
//  }
//}
