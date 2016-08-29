package slicky.entity

import commons.logger._
import org.scalatest.{FlatSpec, FunSuite}
import slicky.Slicky._
import driver.api._

/*
sbt "~rzepawCommons/testOnly slicky.entity.EntityTest"
 */

class EntityTest
  extends FlatSpec
    with Logger {

  NameValue.table.schema.create.await

  "Entity stream" should "have 3 elements" in {

    val in1 = NameValue("one", 1).insert.await
    val in2 = NameValue("two", 2).insert.await
    val in3 = NameValue("three", 3).insert.await
    assert(NameValue.stream.toList.size == 3)

    NameValue.stream.foreach { e =>
      println(" - " + e)
    }
  }

  it should "nothing" in {
    (1 to 10) map { i =>
      NameValue(f"NV:$i%03d", i).insert.await
    }
    NameValue.stream.foreach { nv =>
      println(" *** " + nv)
    }
  }

  "Entity stream page" should "have 10 distinct elements" in {

    NameValue.deleteAll().await

    val PAGE_SIZE = 3

    (1 to 10) map { i =>
      NameValue(f"NV:$i%03d", i).insert.await
    }

    val pages = NameValue.pages(PAGE_SIZE).await
    println(s"Pages: $pages")
    assert(pages == 4)

    val all: Seq[NameValue] = (0 to 10) flatMap { page =>
      val nvs = NameValue.page(page, PAGE_SIZE).await
      println(s"P$page: " + nvs)
      nvs
    }

    assert(all.distinct.size == 10)
  }

  val query123 = NameValue.table.filter(_.value === 123)
  "After get or insert stream" should "have 1 element" in {
    NameValue("asdf", 123).getOrInsert(query123).await
    val results: Seq[NameValue] = query123.result.await
    println(s"Results: $results")
    assert(results.size == 1)
  }
  it should "have 1 element after get or insert" in {
    NameValue("qwer", 123).getOrInsert(query123).await
    val results: Seq[NameValue] = query123.result.await
    println(s"Results: $results")
    assert(results.size == 1)
  }
  it should "have the old name" in {
    val results: Seq[NameValue] = query123.result.await
    println(s"Results: $results")
    assert(results.head.name == "asdf")
  }
  it should "have 1 element after update or insert" in {
    NameValue("qwer", 123).updateOrInsert(query123).await
    val results: Seq[NameValue] = query123.result.await
    println(s"Results: $results")
    assert(results.size == 1)
  }
  it should "have the new name" in {
    val results: Seq[NameValue] = query123.result.await
    println(s"Results: $results")
    assert(results.head.name == "qwer")
  }
}

case class NameValue(var name: String, var value: Int)
  extends Entity[NameValue](NameValue)

object NameValue
  extends EntityMeta[NameValue] {

  val table = TableQuery[Tbl]

  class Tbl(tag: Tag)
    extends EntityTable(tag) {

    def name = column[String]("NAME")
    def value = column[Int]("VALUE")

    def * = (name, value) <>
      ((NameValue.apply _).tupled, NameValue.unapply)
  }
}
