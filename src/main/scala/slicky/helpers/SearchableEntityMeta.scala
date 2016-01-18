package slicky.helpers

import commons.logger.Logger
import slick.lifted.CanBeQueryCondition
import slicky.entity.{EntityMeta, Entity}
import slicky.Slicky._
import driver.api._

import scala.concurrent.Future

trait SearchableEntityMeta[E <: Entity[E]]
  extends Logger {

  self: EntityMeta[E] =>

  protected def dbLikeQueryString(string: String): String = s"%${ string.toLowerCase }%"

  def likeQueryString(s: String) = s"%${ s.toLowerCase }%"

  def searchQuery(query: String): Query[EntityTable, E, Seq]

  def searchPage(query: Option[String], pageNum: Int, pageSize: Int): Future[Seq[E]] =
    query match {
      case Some(q) if q.nonEmpty =>
        page(searchQuery(q), pageNum, pageSize)
      case _ =>
        warn("Search query is empty")
        page(allQuery, pageNum, pageSize)
    }

//  def filterByQuery(query: Query[T, E, Seq])(f: T => CanBeQueryCondition) =
//  query.withFilter(_.na)
//    query.withFilter(f)
}
