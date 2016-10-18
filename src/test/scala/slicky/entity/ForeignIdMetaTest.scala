package slicky.entity

import org.scalatest.FlatSpec
import slicky.Slicky._
import driver.api._
import slicky.helpers.ForeignIdEntityMeta

/*
sbt "~rzepaw-commons/testOnly slicky.entity.ForeignIdMetaTest"
 */

class ForeignIdMetaTest
  extends FlatSpec {

  NameFID.table.schema.create.await
  NameFID("0", None).insert.await
  NameFID("1", Some("1")).insert.await
  NameFID("2a", Some("2")).insert.await
  NameFID("2b", Some("2")).insert.await

  "By foreign id" should "return proper entity" in {
    assert(NameFID.withNoForeignId.await.size == 1)
    assert(NameFID.byForeignId("1").await.size == 1)
    assert(NameFID.byForeignId("2").await.size == 2)
  }
}

case class NameFID(var name: String, var foreignId: Option[String])
  extends Entity[NameFID](NameFID) {

  def withForeignId(foreignId: String) = this.copy(foreignId = Some(foreignId))
}

object NameFID
  extends EntityMeta[NameFID]
  with ForeignIdEntityMeta[NameFID] {

  val table = TableQuery[Tbl]

  class Tbl(tag: Tag)
    extends EntityTable(tag) {

    def name = column[String]("NAME")
    def foreignId = column[Option[String]]("FOREIGN_ID")

    def * = (name, foreignId) <>
      ((NameFID.apply _).tupled, NameFID.unapply)
  }
}
