package slicky.helpers

import org.joda.time.DateTime

import scala.concurrent.Future
import slicky.Slicky._

trait VersionableEntity[TE <: TreeEntity[TE] with CreatedableEntity]
  extends CreatedableEntity { self: TreeEntity[TE] with CreatedableEntity =>

  def actualVersion: Future[TreeEntity[TE]] =
    descendants.map(_.sortBy(_.created.getMillis).headOption.getOrElse(this)).future
}
