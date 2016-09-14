package slicky.entity

import java.util.UUID

import commons.logger._
import commons.random.Rand
import org.scalatest.FunSuite
import slicky.Slicky._
import driver.api._

/*
sbt "~rzepawCommons/testOnly slicky.entity.UUIDEntityTest"
 */

class UUIDEntityTest
  extends FunSuite
    with Logger {

  (UuidName.table.schema.create >>
    UuidValue.table.schema.create).await

  test("Insert entity") {

    val in1 = UuidName("one").insert.await
    val in2 = UuidName("two").save.await
    val in3 = UuidName("three").save.await
    assert(UuidName.stream.toList.size == 3)

    UuidName.stream.foreach { e =>
      println(" - " + e)
    }

    in1.name = "ONE"
    assert(in1.update.await.name == "ONE")
    assert(UuidName.byIdent(in1.id).await.get.name == "ONE")
  }

  test("Update or insert with id") {
    val in1 = UuidName("one").updateOrInsert(UuidName.table.filter(_.name === "one")).await
    assert(in1.id.isDefined)
    println(UuidName.stream.toList)
    val in2 = UuidName("one").updateOrInsert(UuidName.table.filter(_.name === "one")).await
    assert(in2.id.isDefined)
    assert(in1.id == in2.id)
    println(UuidName.stream.toList)
  }

  test("Polymorphic filter") {
    val nUUID: UUID = UuidName("a").insert.await.ident
    val vUUID: UUID = UuidValue(1).insert.await.ident
    //assert(UUIDEntities.byIdent(nUUID).get.isInstanceOf[UuidName])
    //assert(UUIDEntities.byIdent(vUUID).get.isInstanceOf[UuidValue])
    val randomUUID: UUID = Rand.one(List(nUUID, vUUID))
    val entity = UUIDEntities.byIdent(randomUUID)
    assert(entity.isDefined)
  }
}

// Name

case class UuidName(var name: String,
                    id: Option[UUID] = None)
  extends UUIDEntity[UuidName](UuidName) {

  override def withId(id: UUID) = this.copy(id = Some(id))
}

object UuidName
  extends UUIDEntityMeta[UuidName]("UUID_NAME") {

  val table = TableQuery[Tbl]

  class Tbl(tag: Tag) extends EntityTableWithUUID(tag) {

    def name = column[String]("NAME")
    def id = column[Option[UUID]]("UUID", O.PrimaryKey)

    def * = (name, id) <>
      ((UuidName.apply _).tupled, UuidName.unapply)
  }
}

// Value

case class UuidValue(var value: Long,
                     id: Option[UUID] = None)
  extends UUIDEntity[UuidValue](UuidValue) {

  override def withId(id: UUID) = this.copy(id = Some(id))
}

object UuidValue
  extends UUIDEntityMeta[UuidValue]("UUID_VALUE") {

  val table = TableQuery[Tbl]

  class Tbl(tag: Tag) extends EntityTableWithUUID(tag) {

    def value = column[Long]("VALUE")
    def id = column[Option[UUID]]("UUID", O.PrimaryKey)

    def * = (value, id) <>
      ((UuidValue.apply _).tupled, UuidValue.unapply)
  }
}
