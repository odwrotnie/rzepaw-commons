package slicky

import commons.logger._
import org.scalatest.FunSuite
import slicky.Slicky._
import driver.api._
import slicky.entity.Entity
import slicky.entity.EntityMeta

/*
sbt "~rzepawCommons/testOnly slicky.SlickyTest"
 */

class SlickyTest
  extends FunSuite
  with Logger {

  dbAwait {
    NameValue.table.schema.create
  }

  test("Streamify") {
    dbFutureSeq {
      (1 to 10) map { i =>
        NameValue(i.toString, i).insert
      }
    }.await
    val stream = Slicky.streamify(NameValue.table, 3)
    stream foreach { nv =>
      println(s" - $nv")
    }
  }
}

case class NameValue(var name: String, var value: Int)
  extends Entity[NameValue](NameValue)

object NameValue
  extends EntityMeta[NameValue]("NAME_VALUE") {

  val table = TableQuery[Tbl]

  class Tbl(tag: Tag)
    extends Table[NameValue](tag, tableName) {

    def name = column[String]("NAME")
    def value = column[Int]("VALUE")

    def * = (name, value) <>
      ((NameValue.apply _).tupled, NameValue.unapply)
  }
}
