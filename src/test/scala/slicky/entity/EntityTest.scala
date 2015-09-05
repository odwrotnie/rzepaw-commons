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
    NameValue.table.schema.create
  }

  test("Insert entity") {

    val in1 = NameValue("one", 1).insert.await
    val in2 = NameValue("two", 2).insert.await
    val in3 = NameValue("three", 3).insert.await
    assert(NameValue.stream.toList.size == 3)

    NameValue.stream.foreach { e =>
      println(" - " + e)
    }
  }
}

case class NameValue(var name: String, var value: Int)
  extends Entity[NameValue](NameValue)

object NameValue
  extends EntityMeta[NameValue] {

  val table = TableQuery[Tbl]

  class Tbl(tag: Tag)
    extends Table[NameValue](tag, "NAME") {

    def name = column[String]("NAME")
    def value = column[Int]("VALUE")

    def * = (name, value) <>
      ((NameValue.apply _).tupled, NameValue.unapply)
  }
}
