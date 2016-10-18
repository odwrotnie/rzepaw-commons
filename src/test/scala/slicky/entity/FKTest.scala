package slicky.entity

import commons.reflection.Spiegel
import org.scalatest.FlatSpec
import slicky.Slicky._
import driver.api._
import slicky.fields._
import scala.concurrent.Future

/*
sbt "~rzepaw-commons/testOnly slicky.entity.FKTest"
 */

class FKTest
  extends FlatSpec {

  Foo.table.schema.create.await
  Bar.table.schema.create.await

  val foo1 = Foo("foo1").insert.await
  val foo2 = Foo("foo2").insert.await
  val bar = Bar("bar", foo1.ident).insert.await

  "Bar" should "have foo" in {
    assert(bar.foo.entity.await === foo1)
  }

  "Bar from DB" should "have foo" in {
    assert(Bar.table.result.await.head.foo == foo1.ident)
  }

  "The query" should "return bar" in {
    val query = Bar.table.filter(_.foo === foo1.ident)
    val results = query.result.await
    println(s"Res: $results")
    assert(results.contains(bar))
  }
}

//

case class Bar(var name: String,
               var foo: ID[Foo],
               id: Option[ID[Bar]] = None)
  extends IdEntity[Bar](Bar) {
  override def withId(id: Option[ID[Bar]]) = this.copy(id = id)
}

object Bar
  extends IdEntityMeta[Bar] {
  val table = TableQuery[Tbl]
  class Tbl(tag: Tag) extends EntityTableWithId(tag) {
    def name = column[String]("NAME")
    def foo = column[ID[Foo]]("FOO")
    def id = column[ID[Bar]]("ID", O.PrimaryKey, O.AutoInc)
    def * = (name, foo, id.?) <>
      ((Bar.apply _).tupled, Bar.unapply)
  }
}

//

case class Foo(var name: String,
               id: Option[ID[Foo]] = None)
  extends IdEntity[Foo](Foo) {
  override def withId(id: Option[ID[Foo]]) = this.copy(id = id)
}

object Foo
  extends IdEntityMeta[Foo] {
  val table = TableQuery[Tbl]
  class Tbl(tag: Tag) extends EntityTableWithId(tag) {
    def name = column[String]("NAME")
    def id = column[ID[Foo]]("ID", O.PrimaryKey, O.AutoInc)
    def * = (name, id.?) <>
      ((Foo.apply _).tupled, Foo.unapply)
  }
}
