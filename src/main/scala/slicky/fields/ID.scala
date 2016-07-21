package slicky.fields

import commons.reflection.Spiegel
import slicky.Slicky._
import driver.api._
import slicky.entity._
import scala.concurrent.Future
import scala.reflect.runtime.universe._
import scala.util

trait AnyID {
  def value: Long
}

case class ID[E <: IdEntity[E]](value: Long)(implicit tag: TypeTag[E])
  extends MappedTo[Long]
  with AnyID {

  def meta: Option[IdEntityMeta[E]] = util.Try(Spiegel.companion[E].asInstanceOf[IdEntityMeta[E]]).toOption

  lazy val entityOption: Option[DBIO[E]] = meta.map(_.byIdentGet(this))
  lazy val entity: DBIO[E] = entityOption.get

  override def toString = value.toString
}

object ID {
  def apply[E <: IdEntity[E]](any: { def id: Long })(implicit tag: TypeTag[E]): ID[E] =
    ID[E](any.id)
  def apply[E <: IdEntity[E]](any: { def id: Option[Long] })(implicit tag: TypeTag[E]): Option[ID[E]] =
    any.id.map(id => ID[E](id))
//  def apply[E <: IdEntity[E]](any: Option[{ def id: Long }])(implicit tag: TypeTag[E]): Option[ID[E]] =
//    any.map(any => ID[E](any.id))
  def apply[E <: IdEntity[E]](any: Option[{ def id: Option[Long] }])(implicit tag: TypeTag[E]): Option[ID[E]] =
    for {a <- any; id <- a.id} yield ID[E](id)
}
