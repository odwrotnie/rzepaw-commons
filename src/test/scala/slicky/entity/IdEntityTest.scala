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
    NumberNameMeta.table.schema.create
  }

  test("Insert entity") {

  }
}

case class IdName(name: String,
                  var id: Option[ID])
  extends IdEntity[IdName](NumberNameMeta) {
}


object NumberNameMeta
  extends IdEntityMeta[IdName] {

  val table = TableQuery[Tbl]

  class Tbl(tag: Tag)
    extends Table[IdName](tag, "ID_NAME") {

    def name = column[String]("NAME")
    def id = column[ID]("ID", O.PrimaryKey, O.AutoInc)

    def * = (name, id.?) <>
      ((IdName.apply _).tupled, IdName.unapply)
  }
}
