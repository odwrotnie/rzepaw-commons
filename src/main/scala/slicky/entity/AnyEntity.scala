package slicky.entity

import commons.text.Slugify
import slicky.Slicky._
import driver.api._

import scala.concurrent.Future

trait AnyEntity {

  def meta: AnyEntityMeta
  def insert: DBIO[AnyEntity]
}

trait AnyEntityMeta {

  val tableName: String = Slugify(getClass.getSimpleName, "_").toUpperCase
  def table: TableQuery[_]

  def count: Int = table.size.result.await
  def stream: Stream[AnyEntity]

  def page(pageNum: Int, pageSize: Int): Future[Seq[AnyEntity]]
  def pages(pageSize: Int): Future[Long]
}
