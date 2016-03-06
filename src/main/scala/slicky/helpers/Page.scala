package slicky.helpers

import slicky.Slicky
import slicky.Slicky._
import driver.api._
import slicky.entity.Entity

import scala.concurrent.Future

case class Page[E <: Entity[E]](num: Long, query: Query[_, E, Seq],
                                pageSize: Int = 10) {

  lazy val count: Future[Long] = pages(query)

  lazy val hasNext: Boolean = count.map(_ > num + 1).await
  lazy val hasPrev: Boolean = num > 0

  lazy val next: Option[Page[E]] = if (hasNext)
    Some(Page(num + 1, query, pageSize))
  else None
  lazy val prev: Option[Page[E]] = if (hasPrev)
    Some(Page(num - 1, query, pageSize))
  else None

  lazy val results = Slicky.page(query, num, pageSize)

  def page(num: Long = 0): Option[Page[E]] = count.map(_ > num + 1).await match {
    case true => Some(Page(num, query, pageSize))
    case false => None
  }
}
