package slicky

import commons.logger._
import commons.random.Lipsum
import org.scalatest.{FlatSpec, FunSuite}
import slicky.Slicky._
import driver.api._
import slicky.entity.Entity
import slicky.entity.EntityMeta

/*
sbt "~rzepaw-commons/testOnly slicky.SlickyTest"
 */

class SlickyTest
  extends FlatSpec
    with Logger {

  NameValue.table.schema.create.await

  "Streamify" should "work" in {
      (1 to 10) map { i =>
        NameValue(i.toString, i).insert.await
      }
    val stream = Slicky.streamify(NameValue.table, 3)
    stream foreach { nv =>
      println(s" - $nv")
    }
  }

  "Page" should "be sorted" in {
    (1 to 10000) foreach { i =>
      NameValue(Lipsum.generate(1, 3), i).insert.await
    }
    val l = NameValue("L", 0).insert.await
    val query = NameValue.table
      .filter(_.name.startsWith("L"))
      .sortBy(_.name)
      .sortBy(_.value)
    val results = Slicky.page(query, 0, 10).await
    println(results.mkString(", "))
    assert(results.nonEmpty)
    assert(results.head == l)
    assert(results.size <= 10)
  }
}

case class NameValue(var name: String, var value: Int)
  extends Entity[NameValue](NameValue)

object NameValue
  extends EntityMeta[NameValue] {

  val table = TableQuery[Tbl]

  class Tbl(tag: Tag) extends EntityTable(tag) {

    def name = column[String]("NAME")
    def value = column[Int]("VALUE")

    def * = (name, value) <>
      ((NameValue.apply _).tupled, NameValue.unapply)
  }
}
