package slicky.entity

import commons.reflection.Spiegel
import org.scalatest.FlatSpec
import slicky.Slicky._
import driver.api._
import slicky.fields._
import scala.concurrent.Future

/*
sbt "~rzepawCommons/testOnly slicky.entity.FKTest"
 */

class FKTest
  extends FlatSpec {

  Foo.table.schema.create.await
  Bar.table.schema.create.await

  val foo1 = Foo("foo1").insert.await
  val foo2 = Foo("foo2").insert.await
  val fk1 = FK[Foo](foo1.id)
  val bar = Bar("bar", fk1).insert.await

  assert(bar.foo == fk1)

  "Bar" should "have foo" in {
    assert(bar.foo.entity.await.get === foo1)
  }

  "The query" should "return bar" in {
    val query = Bar.table.filter(_.foo === fk1)
    val results = query.result.await
    println(s"Res: $results")
    assert(results.contains(bar))
  }
}

//

case class Bar(var name: String,
               var foo: FK[Foo],
               id: Option[ID] = None)
  extends IdEntity[Bar](Bar) {
  override def withId(id: Option[ID]) = this.copy(id = id)
}

object Bar
  extends IdEntityMeta[Bar] {
  val table = TableQuery[Tbl]
  class Tbl(tag: Tag) extends EntityTableWithId(tag) {

    implicit val fooFKMapper = FK.mapper[Foo]

    def name = column[String]("NAME")
    def foo = column[FK[Foo]]("FOO")
    def id = column[ID]("ID", O.PrimaryKey, O.AutoInc)
    def * = (name, foo, id.?) <>
      ((Bar.apply _).tupled, Bar.unapply)
  }
}

//

case class Foo(var name: String,
               id: Option[ID] = None)
  extends IdEntity[Foo](Foo) {
  override def withId(id: Option[ID]) = this.copy(id = id)
}

object Foo
  extends IdEntityMeta[Foo] {
  val table = TableQuery[Tbl]
  class Tbl(tag: Tag) extends EntityTableWithId(tag) {
    def name = column[String]("NAME")
    def id = column[ID]("ID", O.PrimaryKey, O.AutoInc)
    def * = (name, id.?) <>
      ((Foo.apply _).tupled, Foo.unapply)
  }
}
