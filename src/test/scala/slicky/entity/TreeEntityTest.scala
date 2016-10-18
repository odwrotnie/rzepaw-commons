package slicky.entity

import commons.logger._
import org.scalatest.FunSuite
import slicky.Slicky._
import driver.api._
import slicky.fields.ID
import slicky.helpers.{TreeEntityMeta, TreeEntity}

/*
sbt "~rzepaw-commons/testOnly slicky.entity.TreeEntityTest"
*/

class TreeEntityTest
  extends FunSuite
  with Logger {

  TreeName.table.schema.create.await

  test("Insert entity") {

    val in1 = TreeName("one").insert.await
    assert(in1.ident == ID[TreeName](1l))

    val in2 = TreeName("two", in1.id).save.await
    assert(in2.ident == ID[TreeName](2l))

    val in3 = TreeName("three", in2.id).save.await
    assert(TreeName.stream.toList.size == 3)

    TreeName.stream.foreach { e =>
      println(s" - $e path: ${ e.path.await.mkString(" / ") }")
    }

    assert(in1.descendantsCount.await == 2)
    assert(in2.descendantsCount.await == 1)
    assert(in3.descendantsCount.await == 0)
  }
}

case class TreeName(var name: String,
                    var parentId: Option[ID[TreeName]] = None,
                    id: Option[ID[TreeName]] = None)
  extends TreeEntity[TreeName](TreeName) {
  override def withId(id: Option[ID[TreeName]]) = this.copy(id = id)
  override def toString = name
}

object TreeName
  extends TreeEntityMeta[TreeName] {

  val table = TableQuery[Tbl]

  class Tbl(tag: Tag) extends EntityTableWithIdAndParent(tag) {

    def name = column[String]("NAME")
    def parentId = column[Option[ID[TreeName]]]("PARENT")
    def id = column[ID[TreeName]]("ID", O.PrimaryKey, O.AutoInc)

    def * = (name, parentId, id.?) <>
      ((TreeName.apply _).tupled, TreeName.unapply)
  }
}
