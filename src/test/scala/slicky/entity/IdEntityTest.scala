package slicky.entity

import commons.logger._
import org.scalatest.{FlatSpec, FunSuite}
import slicky.Slicky._
import driver.api._
import slicky.helpers.Evolutions

/*
sbt "~rzepawCommons/testOnly slicky.entity.IdEntityTest"
 */

class IdEntityTest
  extends FlatSpec
  with Logger {

  dbAwait {
    IdName.table.schema.create
  }

  "Inserted entity" should "have proper name" in {

    val in1 = dbAwait(IdName("one").insert)
    assert(in1.ident == 1l)

    val in2 = dbAwait(IdName("two").save)
    assert(in2.ident == 2l)

    val in3 = dbAwait(IdName("three").save)
    assert(IdName.stream.toList.size == 3)

    IdName.stream.foreach { e =>
      println(" - " + e)
    }

    in1.name = "ONE"
    assert(dbAwait(in1.update).name == "ONE")
    assert(dbAwait(IdName.byIdent(1)).get.name == "ONE")
  }

//  "Update or insert with id" should "have id defined" in {
//    val in1 = dbAwait(IdName("one").updateOrInsert(IdName.table.filter(_.name === "one")))
//    println(IdName.stream.toList)
//    val in2 = dbAwait(IdName("one").updateOrInsert(IdName.table.filter(_.name === "one")))
//    println(IdName.stream.toList)
//
//    assert(in1.id.isDefined)
//    assert(in1.id == in2.id)
//  }

  val queryAsdf = IdName.table.filter(_.name === "asdf")
  val queryQwer = IdName.table.filter(_.name === "qwer")
  var id = -1l
  "After get or insert stream" should "have 1 element" in {
    IdName("asdf").getOrInsert(queryAsdf).await
    val results: Seq[IdName] = queryAsdf.result.await
    println(s"Results: $results")
    assert(results.size == 1)
  }
  it should "have 1 element after get or insert" in {
    IdName("qwer").getOrInsert(queryAsdf).await
    val results: Seq[IdName] = queryAsdf.result.await
    println(s"Results: $results")
    assert(results.size == 1)
  }
  it should "have the old name" in {
    val results: Seq[IdName] = queryAsdf.result.await
    println(s"Results: $results")
    assert(results.head.name == "asdf")
  }
  it should "have 0 elements after update or insert with old name" in {
    IdName("qwer").updateOrInsert(queryAsdf).await
    val results: Seq[IdName] = queryAsdf.result.await
    println(s"Results: $results")
    assert(results.isEmpty)
  }
  it should "have 1 elements after update or insert with new name" in {
    IdName("qwer").updateOrInsert(queryQwer).await
    val results: Seq[IdName] = queryQwer.result.await
    println(s"Results: $results")
    assert(results.size == 1)
  }
  it should "have the new name" in {
    val results: Seq[IdName] = queryQwer.result.await
    println(s"Results: $results")
    assert(results.head.name == "qwer")
  }

  "Updated entity" should "have the same id" in {
    val erty = IdName("erty").updateOrInsert(IdName.table.filter(_.name === "erty")).await
    val yuio = IdName("yuio").updateOrInsert(IdName.table.filter(_.name === "erty")).await
    println(s"Erty: $erty, yuio: $yuio")
    assert(erty.ident == yuio.ident)
  }

  "Evolutions file" should "be created" in {
    Evolutions("evolutions.sql", IdName, IdName, IdName).generate(true)
  }
}

case class IdName(var name: String,
                  id: Option[ID] = None)
  extends IdEntity[IdName](IdName) {

  override def withId(id: Option[ID]) = this.copy(id = id)
}

object IdName
  extends IdEntityMeta[IdName] {

  val table = TableQuery[Tbl]

  class Tbl(tag: Tag) extends EntityTableWithId(tag) {

    def name = column[String]("NAME")
    def id = column[ID]("ID", O.PrimaryKey, O.AutoInc)

    def * = (name, id.?) <>
      ((IdName.apply _).tupled, IdName.unapply)
  }
}
