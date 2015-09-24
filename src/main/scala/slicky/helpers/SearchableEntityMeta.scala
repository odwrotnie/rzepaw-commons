package slicky.helpers

import slicky.entity.{EntityMeta, Entity}
import slicky.Slicky._
import driver.api._

trait SearchableEntityMeta[E <: Entity[E]] {

  self: EntityMeta[E] =>

  def searchQuery(query: String): Query[_, E, Seq]

  def searchStream(query: String): Stream[E] = streamify(searchQuery(query))
}