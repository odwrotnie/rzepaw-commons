package slicky.helpers

import commons.logger.Logger
import slicky.entity.{EntityMeta, Entity}
import slicky.Slicky._
import driver.api._

import scala.concurrent.Future

trait SearchableEntityMeta[E <: Entity[E]]
  extends Logger {

  self: EntityMeta[E] =>

  protected def dbLikeQueryString(string: String): String = s"%${ string.toLowerCase }%"

  def searchQuery(query: String): Query[T, E, Seq]

  def searchPage(query: Option[String], pageNum: Int, pageSize: Int): Future[Seq[E]] =
    query match {
      case Some(q) if q.nonEmpty =>
        page(searchQuery(q), pageNum, pageSize)
      case _ =>
        warn("Search query is empty")
        page(allQuery, pageNum, pageSize)
    }
}
