package slicky.helpers

import slicky.Slicky._
import driver.api._
import slicky.entity.Entity
import scala.concurrent.Future

trait ForeignIdMeta[E <: Entity[E]] {
  type FIT = Table[E] { def foreignId: Rep[Option[String]] }
  def table: TableQuery[_ <: FIT]
  def byForeignId(foreignId: String): Stream[E] = streamify {
    table.filter(_.foreignId === foreignId)
  }
}
