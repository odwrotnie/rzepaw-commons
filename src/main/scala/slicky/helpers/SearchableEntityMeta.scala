package slicky.helpers

import slicky.entity.{EntityMeta, Entity}
import slicky.Slicky._
import driver.api._

import scala.concurrent.Future

trait SearchableEntityMeta[E <: Entity[E]] {

  self: EntityMeta[E] =>

  def searchQuery(query: String): Query[T, E, Seq]

  def searchPage(query: String, pageNum: Int, pageSize: Int): Future[Seq[E]] =
    page(searchQuery(query), pageNum, pageSize)
}
