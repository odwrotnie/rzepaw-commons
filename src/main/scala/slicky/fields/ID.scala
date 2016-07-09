package slicky.fields

import commons.reflection.Spiegel
import slicky.Slicky._
import driver.api._
import slicky.entity._
import scala.concurrent.Future
import scala.reflect.runtime.universe._

case class ID[E <: IdEntity[E]](value: Long)(implicit tag: TypeTag[E]) extends MappedTo[Long] {

  lazy val meta: IdEntityMeta[E] = Spiegel.companion[E].asInstanceOf[IdEntityMeta[E]]
  lazy val entity: Future[E] = meta.byIdentGet(this).future

  override def toString = value.toString
}
