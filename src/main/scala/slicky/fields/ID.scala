package slicky.fields

import commons.reflection.Spiegel
import slicky.Slicky._
import driver.api._
import slicky.entity._
import scala.concurrent.Future
import scala.reflect.runtime.universe._
import scala.util

case class ID[E <: IdEntity[E]](value: Long)(implicit tag: TypeTag[E]) extends MappedTo[Long] {

  def meta: Option[IdEntityMeta[E]] = util.Try(Spiegel.companion[E].asInstanceOf[IdEntityMeta[E]]).toOption

  lazy val entityOption: Option[DBIO[E]] = meta.map(_.byIdentGet(this))
  lazy val entity: DBIO[E] = entityOption.get

  override def toString = value.toString
}

//object ID {
//  def apply[E <: IdEntity[E]](entity: E): ID[E] = entity.ident
//}
