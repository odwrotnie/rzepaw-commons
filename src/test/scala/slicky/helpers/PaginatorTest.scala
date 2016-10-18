package slicky.helpers

import slicky.Slicky._
import driver.api._
import org.scalatest.{Matchers, FlatSpec}
import slicky.entity._

/*
sbt "~rzepaw-commons/testOnly slicky.helpers.PaginatorTest"
 */

class PaginatorTest
  extends FlatSpec
    with Matchers {

  NameValue.table.schema.create.await

  (1 to 3) foreach { i =>
    (1 to 7) foreach { j =>
      NameValue(s"NV:$i/$j", i).insert.await
    }
  }

  val query = NameValue.table.sortBy(_.value)
  val firstPage = Page(0, query, 7)
  val secondPage = Page(1, query, 7)
  val thirdPage = Page(2, query, 7)

  "First page" should "have only ones" in {
    firstPage.results.await.exists(_.value != 1) should be (false)
  }
  it should "have no prev but next" in {
    firstPage.prev.isDefined should equal (false)
    firstPage.next.isDefined should equal (true)
    firstPage.hasPrev should equal (false)
    firstPage.hasNext should equal (true)
  }

  "Second page" should "have only twos" in {
    secondPage.results.await.exists(_.value != 2) should be (false)
  }

  "Second page" should "equal firsts next" in {
    secondPage should equal (firstPage.next.get)
  }
  it should "have next and prev" in {
    secondPage.prev.isDefined should equal (true)
    secondPage.next.isDefined should equal (true)
    secondPage.hasPrev should equal (true)
    secondPage.hasNext should equal (true)
  }

  "Third page" should "have no next but prev" in {
    thirdPage.prev.isDefined should equal (true)
    thirdPage.next.isDefined should equal (false)
    thirdPage.hasPrev should equal (true)
    thirdPage.hasNext should equal (false)
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
