package slicky.entity

import java.util.UUID

import commons.logger._
import org.scalatest.FunSuite
import slicky.Slicky._
import driver.api._

/*
sbt "~rzepawCommons/testOnly slicky.entity.UUIDEntityTest"
 */

class UUIDEntityTest
  extends FunSuite
  with Logger {

  dbAwait {
    UuidName.table.schema.create
  }

  test("Insert entity") {

    val in1 = dbAwait(UuidName("one").insert)
    val in2 = dbAwait(UuidName("two").save)
    val in3 = dbAwait(UuidName("three").save)
    assert(UuidName.stream.toList.size == 3)

    UuidName.stream.foreach { e =>
      println(" - " + e)
    }

    in1.name = "ONE"
    assert(dbAwait(in1.update).name == "ONE")
    assert(dbAwait(UuidName.byIdent(in1.id)).get.name == "ONE")
  }

  test("Update or insert with id") {
    val in1 = dbAwait(UuidName("one").updateOrInsert(UuidName.table.filter(_.name === "one")))
    println(UuidName.stream.toList)
    val in2 = dbAwait(UuidName("one").updateOrInsert(UuidName.table.filter(_.name === "one")))
    println(UuidName.stream.toList)

    assert(in1.id.isDefined)
    assert(in1.id == in2.id)
  }
}

case class UuidName(var name: String,
                    id: Option[UUID] = None)
  extends UUIDEntity[UuidName](UuidName) {

  override def withId(id: UUID) = this.copy(id = Some(id))
}

object UuidName
  extends UUIDEntityMeta[UuidName] {

  val table = TableQuery[Tbl]

  class Tbl(tag: Tag)
    extends Table[UuidName](tag, "ID_NAME") {

    def name = column[String]("NAME")
    def id = column[Option[UUID]]("UUID", O.PrimaryKey)

    def * = (name, id) <>
      ((UuidName.apply _).tupled, UuidName.unapply)
  }
}
