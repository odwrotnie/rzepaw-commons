package slicky.entity

import commons.logger._
import org.scalatest.FunSuite
import slicky.Slicky._
import driver.api._
import slicky.helpers.{TreeEntityMeta, TreeEntity}

// sbt "~rzepawCommons/testOnly slicky.entity.TreeEntityTest"
class TreeEntityTest
  extends FunSuite
  with Logger {

  dbAwait {
    TreeName.table.schema.create
  }

  test("Insert entity") {

    val in1 = TreeName("one").insert.await
    assert(in1.ident == 1l)

    val in2 = TreeName("two", in1.id).save.await
    assert(in2.ident == 2l)

    val in3 = TreeName("three", in2.id).save.await
    assert(TreeName.stream.toList.size == 3)

    TreeName.stream.foreach { e =>
      println(s" - $e path: ${ e.path.await.mkString(" / ") }")
    }
  }
}

case class TreeName(var name: String,
                    var parentId: Option[ID] = None,
                    id: Option[ID] = None)
  extends TreeEntity[TreeName](TreeName) {
  override def withId(id: ID) = this.copy(id = Some(id))
  override def toString = name
}

object TreeName
  extends TreeEntityMeta[TreeName] {

  val table = TableQuery[Tbl]

  class Tbl(tag: Tag)
    extends Table[TreeName](tag, "ID_NAME") {

    def name = column[String]("NAME")
    def parentId = column[Option[ID]]("PARENT")
    def id = column[ID]("ID", O.PrimaryKey, O.AutoInc)

    def * = (name, parentId, id.?) <>
      ((TreeName.apply _).tupled, TreeName.unapply)
  }
}
