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
  val bar = Bar("bar", FooFK(foo)).insert.await

  "Bar" should "have foo" in {
    bar.foo.entity
  }
}

//

case class FooFK(entity: Foo)
  extends FK[Foo](entity)

object FooFK {
  implicit val FKMapper = MappedColumnType.base[FooFK, ID](
    fooFK => fooFK.id.get,
    id => FooFK(Foo.byIdentGet(id).await)
  )
}

//

case class Bar(var name: String,
                 var foo: FooFK,
                 id: Option[ID] = None)
  extends IdEntity[Bar](Bar) {
  override def withId(id: ID) = this.copy(id = Some(id))
}

object Bar
  extends IdEntityMeta[Bar] {
  val table = TableQuery[Tbl]
  class Tbl(tag: Tag) extends EntityTableWithId(tag) {

    import FooFK.FKMapper._

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
  override def withId(id: ID) = this.copy(id = Some(id))
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
