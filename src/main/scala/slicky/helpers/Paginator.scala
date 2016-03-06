package slicky.helpers

import slicky.Slicky
import slicky.Slicky._
import driver.api._
import slicky.entity.Entity

import scala.concurrent.Future

case class Page[E <: Entity[E]](paginator: Paginator[E], num: Long) {
  lazy val count: Future[Long] = paginator.count
  lazy val hasNext: Boolean = count.map(_ > num).await
  lazy val next: Option[Page[E]] = if (hasNext)
    Some(Page(paginator, num + 1))
  else None
  lazy val hasPrev: Boolean = num > 0
  lazy val prev: Option[Page[E]] = if (hasPrev)
    Some(Page(paginator, num - 1))
  else None
  lazy val results = Slicky.page(paginator.query, num, paginator.pageSize)
}

case class Paginator[E <: Entity[E]](query: Query[_, E, Seq], pageSize: Int = 10) {
  def count: Future[Long] = pages(query)
  def page(num: Long = 0): Page[E] = Page(this, num)
}
