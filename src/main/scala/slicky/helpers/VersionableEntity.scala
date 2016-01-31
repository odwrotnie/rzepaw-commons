package slicky.helpers

import org.joda.time.DateTime
import slicky.entity.IdEntity

import scala.concurrent.Future
import slicky.Slicky._
import driver.api._

trait VersionableEntity[E <: IdEntity[E] with CreatedableEntity]
  extends CreatedableEntity { self: IdEntity[E] with CreatedableEntity =>

  //def meta: VersionableEntityMeta[E]

  def previousVersionId: Option[ID]
  //def previousVersion: Option[E] = previousVersionId.map(id => meta.byIdentGet(id).future)

  def deepCopy: Future[E]

//  def actualVersion: Future[E] =
//    descendants.map(_.sortBy(_.created.getMillis).headOption.getOrElse(this)).future
//
//  meta.table.filter(_.pre)
}

//trait VersionableEntityMeta[E <: VersionableEntity[E]] {
//  type VT = Table[E] { def previousVersionId: Rep[Option[ID]] }
//  def table: TableQuery[_ <: VT]
//}
