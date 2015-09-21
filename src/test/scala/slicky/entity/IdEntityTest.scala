package slicky.entity

import commons.logger._
import org.scalatest.FunSuite
import slicky.Slicky._
import driver.api._

/*
sbt "~rzepawCommons/testOnly slicky.entity.IdEntityTest"
 */

class IdEntityTest
  extends FunSuite
  with Logger {

  dbAwait {
    IdName.table.schema.create
  }

  ignore("Insert entity") {

    val in1 = IdName("one").insert.await
    assert(in1.ident == 1l)

    val in2 = IdName("two").save.await
    assert(in2.ident == 2l)

    val in3 = IdName("three").save.await
    assert(IdName.stream.toList.size == 3)

    IdName.stream.foreach { e =>
      println(" - " + e)
    }

    in1.name = "ONE"
    assert(in1.update.await.name == "ONE")
    assert(IdName.byIdent(1).await.get.name == "ONE")
  }

  test("Update or insert with id") {
    val in1 = IdName("one").updateOrInsert(IdName.table.filter(_.name === "one")).await
    println(IdName.stream.toList)
    val in2 = IdName("one").updateOrInsert(IdName.table.filter(_.name === "one")).await
    println(IdName.stream.toList)

    assert(in1.id.isDefined)
    assert(in1.id == in2.id)
  }
}

case class IdName(var name: String,
                  id: Option[ID] = None)
  extends IdEntity[IdName](IdName) {

  override def withId(id: ID) = this.copy(id = Some(id))
}

object IdName
  extends IdEntityMeta[IdName] {

//  override def beforeSave(in: IdName): IdName = {
//    in.name = in.name + " BEFORE_SAVE"
//    in
//  }

  val table = TableQuery[Tbl]

  class Tbl(tag: Tag)
    extends Table[IdName](tag, "ID_NAME") {

    def name = column[String]("NAME")
    def id = column[ID]("ID", O.PrimaryKey, O.AutoInc)

    def * = (name, id.?) <>
      ((IdName.apply _).tupled, IdName.unapply)
  }
}
