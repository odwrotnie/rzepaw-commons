package slicky.entity

import commons.logger._
import org.scalatest.FunSuite
import slicky.Slicky._
import driver.api._

/*
sbt "~rzepawCommons/testOnly slicky.entity.EntityTest"
 */

class EntityTest
  extends FunSuite
  with Logger {

  dbAwait {
    NameValue.table.schema.create
  }

  test("Insert entity") {

    val in1 = dbFuture(NameValue("one", 1).insert).await
    val in2 = dbFuture(NameValue("two", 2).insert).await
    val in3 = dbFuture(NameValue("three", 3).insert).await
    assert(NameValue.stream.toList.size == 3)

    NameValue.stream.foreach { e =>
      println(" - " + e)
    }
  }

  test("Entity stream") {
    dbFutureSeq {
      (1 to 10) map { i =>
        NameValue(f"NV:$i%03d", i).insert
      }
    } await

    NameValue.stream.foreach { nv =>
      println(" *** " + nv)
    }
  }

  test("Entity stream page") {

    dbFuture(NameValue.deleteAll()).await

    val PAGE_SIZE = 3

    dbFutureSeq {
      (1 to 10) map { i =>
        NameValue(f"NV:$i%03d", i).insert
      }
    } await
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
