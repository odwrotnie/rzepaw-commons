package slicky.helpers

import commons.logger.Logger
import slicky.entity.{EntityMeta, Entity}
import slicky.Slicky._
import driver.api._

import scala.concurrent.Future

trait SearchableEntityMeta[E <: Entity[E]]
  extends Logger {

  self: EntityMeta[E] =>

  def searchQuery(query: String): Query[T, E, Seq]

  def searchPage(query: String, pageNum: Int, pageSize: Int): Future[Seq[E]] =
    if (query.nonEmpty) {
      page(searchQuery(query), pageNum, pageSize)
    } else {
      warn("Search query is empty")
      page(allQuery, pageNum, pageSize)
    }
}
