package slicky.entity

import commons.logger._
import org.scalatest.FunSuite
import slicky.Slicky._
import driver.api._

// sbt "~rzepawCommons/testOnly slicky.entity.IdEntityTest"
class IdEntityTest
  extends FunSuite
  with Logger {

  dbAwait {
    IdName.table.schema.create
  }

  test("Insert entity") {

    val in1 = IdName("one").save.await
    val in2 = IdName("two").save.await
    val in3 = IdName("three").save.await
    assert(IdName.stream.toList.size == 3)

    IdName.stream.foreach { e =>
      println(" - " + e)
    }

    in1.name = "ONE"
    in1.save
    assert(IdName.byIdent(1).await.get.name == "ONE")


  }
}

case class IdName(var name: String,
                  var id: Option[ID] = None)
  extends IdEntity[IdName](IdName)

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
