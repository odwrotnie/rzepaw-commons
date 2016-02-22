package slicky.entity

import org.scalatest.FlatSpec
import slicky.Slicky._
import driver.api._
import slicky.fields.FK

import scala.concurrent.Future

/*
sbt "~rzepawCommons/testOnly slicky.entity.FKTest"
 */

class FKTest
  extends FlatSpec {

  Foo.table.schema.create.await
  Bar.table.schema.create.await

  val foo = Foo("foo").insert.await
  val bar = Bar("bar", FooFK(Some(foo))).insert.await

  "Bar" should "have foo" in {
    assert(bar.foo.entity.isDefined)
  }
}

//

case class FooFK(var entity: Option[Foo], var id: Option[ID] = None)
  extends FK[Foo] {
  override def meta = Foo
}

//object FooFK {
//  implicit val FKMapper = MappedColumnType.base[FooFK, ID](
//    fooFK => fooFK.id.get,
//    id => FooFK(None, Some(id))
//  )
//}

//

case class Bar(var name: String,
               var foo: FooFK,
               id: Option[ID] = None)
  extends IdEntity[Bar](Bar) {
  override def withId(id: Option[ID]) = this.copy(id = id)
}

object Bar
  extends IdEntityMeta[Bar] {
  val table = TableQuery[Tbl]
  class Tbl(tag: Tag) extends EntityTableWithId(tag) {

    implicit val FooFKMapper = FK.mapper[FooFK](id => FooFK(None, id = Some(id)), fooFK => fooFK.id.get)

    def name = column[String]("NAME")
    def foo = column[FooFK]("ID_NAME")
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
