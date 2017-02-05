package slicky.entity

import commons.logger._
import org.scalatest.{FlatSpec, FunSuite}
import slicky.Slicky._
import driver.api._
import slicky.fields.ID
import slicky.helpers.Evolutions

/*
sbt "~rzepaw-commons/testOnly slicky.entity.IdEntityTest"
 */

class IdEntityTest
  extends FlatSpec
  with Logger {

  IdName.table.schema.create.await

  "IDs" should "equal" in {
    assert(ID[IdName](123) == ID[IdName](123))
    assert(ID[IdName](123) != ID[IdName](321))
  }

  "Any entity" should "work" in {
    val anyEntity: AnyIdEntity = IdName("asdf")
    case class Klass[E <: AnyEntity](idEntity: E)
    val klass = Klass(anyEntity)
    println(klass)
    def method(idEntity: AnyIdEntity): AnyEntity = {
      println(s"Entity: $idEntity")
      idEntity
    }
    method(anyEntity)
  }

  "Inserted entity" should "have proper name" in {

    val in1 = IdName("one").insert.await
    assert(in1.ident == ID[IdName](1l))

    val in2 = IdName("two").save.await
    assert(in2.ident == ID[IdName](2l))

    val in3 = IdName("three").save.await
    assert(IdName.stream.toList.size == 3)

    IdName.stream.foreach { e =>
      println(" - " + e)
    }

    in1.name = "ONE"
    assert(in1.update.await.name == "ONE")
    assert(IdName.byIdent(ID[IdName](1)).await.get.name == "ONE")
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

  "Get or insert" should "get" in {
    val x = IdName("asdf").insert.await
    val y = IdName.getOrInsert(IdName.table.filter(_.name === "asdf"), IdName("asdf")).await
    assert(x.identNumber == y.identNumber)
  }

  "Get or insert" should "insert" in {
    val y = IdName.getOrInsert(IdName.table.filter(_.name === "NOTPRESENTFORSURE"), IdName("NOTPRESENTFORSURE")).await
    assert(y.identNumber > 0)
  }

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
                  id: Option[ID[IdName]] = None)
  extends IdEntity[IdName](IdName) {

  override def withId(id: Option[ID[IdName]]) = this.copy(id = id)
}

object IdName
  extends IdEntityMeta[IdName] {

  val table = TableQuery[Tbl]

  class Tbl(tag: Tag) extends EntityTableWithId(tag) {

    def name = column[String]("NAME")
    def id = column[ID[IdName]]("ID", O.PrimaryKey, O.AutoInc)

    def * = (name, id.?) <>
      ((IdName.apply _).tupled, IdName.unapply)
  }
}
