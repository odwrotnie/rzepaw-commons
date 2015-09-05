package slicky.entity

import commons.logger._
import org.scalatest.FunSuite
import slicky.Slicky._
import driver.api._

// sbt "~rzepawCommons/testOnly slicky.entity.EntityTest"
class EntityTest
  extends FunSuite
  with Logger {

  dbAwait {
    XYName.table.schema.create
  }

  test("Insert entity") {

    val in1 = XYName("one", 1).insert.await
    val in2 = XYName("two", 2).insert.await
    val in3 = XYName("three", 3).insert.await
    assert(XYName.stream.toList.size == 3)

    XYName.stream.foreach { e =>
      println(" - " + e)
    }
  }
}

case class NameValue(var name: String, var value: Int)
  extends Entity[XYName](XYName)

object NameValue
  extends EntityMeta[XYName] {

  val table = TableQuery[Tbl]

  class Tbl(tag: Tag)
    extends Table[XYName](tag, "NAME") {

    def name = column[String]("NAME")
    def value = column[Int]("VALUE")

    def * = (name, value) <>
      ((XYName.apply _).tupled, XYName.unapply)
  }
}
