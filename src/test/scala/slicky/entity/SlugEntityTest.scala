package slicky.entity

import commons.logger._
import org.scalatest.FlatSpec
import slicky.Slicky._
import slicky.Slicky.driver.api._
import slicky.fields.{AnySLUG, SLUG}
import slicky.helpers.Evolutions

/*
sbt "~rzepaw-commons/testOnly slicky.entity.SlugEntityTest"
 */

class SlugEntityTest
  extends FlatSpec
  with Logger {

  SlugName.table.schema.create.await

  "SLUGs" should "equal" in {
    assert(SLUG[SlugName]("asdf") == SLUG[SlugName]("asdf"))
    assert(SLUG[SlugName]("asdf") != SLUG[SlugName]("qwer"))
  }

  "Any entity" should "work" in {
    val anyEntity: AnySlugEntity = SlugName("asdf", SLUG[SlugName]("a"))
    case class Klass[E <: AnyEntity](idEntity: E)
    val klass = Klass(anyEntity)
    println(klass)
    def method(anySlugEntity: AnySlugEntity): AnyEntity = {
      println(s"Entity: $anySlugEntity")
      anySlugEntity
    }
    method(anyEntity)
  }

  "Inserted entity" should "have proper name" in {

    val slug1 = SLUG.generate[SlugName]
    val sn1 = SlugName("one", slug1).insert.await
    val sn2 = SlugName("two", SLUG.generate[SlugName]).save.await
    val sn3 = SlugName("three", SLUG.generate[SlugName]).save.await

    SlugName.stream.foreach { e =>
      println(" - " + e)
    }

    assert(SlugName.byIdentString(slug1.value).await.get.name == "one")

//    sn1.name = "ONE"
//    assert(sn1.update.await.name == "ONE")
//    assert(SlugName.byIdentString(slug1.value).await.get.name == "ONE")
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

//  "Get or insert" should "get" in {
//    val x = SlugName("asdf").insert.await
//    val y = SlugName.getOrInsert(SlugName.table.filter(_.name === "asdf"), SlugName("asdf")).await
//    assert(x.identNumber == y.identNumber)
//  }
//
//  "Get or insert" should "insert" in {
//    val y = SlugName.getOrInsert(SlugName.table.filter(_.name === "NOTPRESENTFORSURE"), SlugName("NOTPRESENTFORSURE")).await
//    assert(y.identNumber > 0)
//  }

//  val queryAsdf = SlugName.table.filter(_.name === "asdf")
//  val queryQwer = SlugName.table.filter(_.name === "qwer")
//  var id = -1l
//  "After get or insert stream" should "have 1 element" in {
//    SlugName("asdf").getOrInsert(queryAsdf).await
//    val results: Seq[SlugName] = queryAsdf.result.await
//    println(s"Results: $results")
//    assert(results.size == 1)
//  }
//  it should "have 1 element after get or insert" in {
//    SlugName("qwer").getOrInsert(queryAsdf).await
//    val results: Seq[SlugName] = queryAsdf.result.await
//    println(s"Results: $results")
//    assert(results.size == 1)
//  }
//  it should "have the old name" in {
//    val results: Seq[SlugName] = queryAsdf.result.await
//    println(s"Results: $results")
//    assert(results.head.name == "asdf")
//  }
//  it should "have 0 elements after update or insert with old name" in {
//    SlugName("qwer").updateOrInsert(queryAsdf).await
//    val results: Seq[SlugName] = queryAsdf.result.await
//    println(s"Results: $results")
//    assert(results.isEmpty)
//  }
//  it should "have 1 elements after update or insert with new name" in {
//    SlugName("qwer").updateOrInsert(queryQwer).await
//    val results: Seq[SlugName] = queryQwer.result.await
//    println(s"Results: $results")
//    assert(results.size == 1)
//  }
//  it should "have the new name" in {
//    val results: Seq[SlugName] = queryQwer.result.await
//    println(s"Results: $results")
//    assert(results.head.name == "qwer")
//  }

//  "Updated entity" should "have the same id" in {
//    val erty = SlugName("erty").updateOrInsert(SlugName.table.filter(_.name === "erty")).await
//    val yuio = SlugName("yuio").updateOrInsert(SlugName.table.filter(_.name === "erty")).await
//    println(s"Erty: $erty, yuio: $yuio")
//    assert(erty.ident == yuio.ident)
//  }

//  "Evolutions file" should "be created" in {
//    Evolutions("evolutions.sql", SlugName, SlugName, SlugName).generate(true)
//  }
}

case class SlugName(var name: String,
                    slug: SLUG[SlugName] = SLUG[SlugName](""))
  extends SlugEntity[SlugName](SlugName) {

  override def withSlug(slug: SLUG[SlugName]) = this.copy(slug = slug)
}

object SlugName
  extends SlugEntityMeta[SlugName] {

  val table = TableQuery[Tbl]

  class Tbl(tag: Tag) extends EntityTableWithSlug(tag) {

    def name = column[String]("NAME")
    def slug = column[SLUG[SlugName]]("SLUG", O.PrimaryKey)

    def * = (name, slug) <>
      ((SlugName.apply _).tupled, SlugName.unapply)
  }
}
