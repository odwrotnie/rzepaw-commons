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
  def meta: Option[AnyIdEntityMeta]
}

class ID[E <: IdEntity[E]](val value: Long)(implicit tag: TypeTag[E])
  extends MappedTo[Long]
    with AnyID {

  def meta: Option[IdEntityMeta[E]] = util.Try(Spiegel.companion[E].asInstanceOf[IdEntityMeta[E]]).toOption

  lazy val entityOption: Option[DBIO[E]] = meta.map(_.byIdentGet(this))
  lazy val entity: DBIO[E] = entityOption.get

  override def equals(obj: scala.Any): Boolean = obj match {
    case id: ID[_] => value.equals(id.value)
    case _ => false
  }
  override def hashCode(): Int = value.hashCode

  override def toString = value.toString
}

object ID {

  def apply[E <: IdEntity[E]](id: Long)(implicit tag: TypeTag[E]): ID[E] = new ID[E](id)
  def apply[E <: IdEntity[E]](any: { def id: Long })(implicit tag: TypeTag[E]): ID[E] = new ID[E](any.id)
  def apply[E <: IdEntity[E]](any: { def id: Option[Long] })(implicit tag: TypeTag[E]): Option[ID[E]] = any.id.map(id => new ID[E](id))
  //  def apply[E <: IdEntity[E]](any: Option[{ def id: Long }])(implicit tag: TypeTag[E]): Option[ID[E]] = any.map(any => ID[E](any.id))
  def apply[E <: IdEntity[E]](any: Option[{ def id: Option[Long] }])(implicit tag: TypeTag[E]): Option[ID[E]] = for {a <- any; id <- a.id} yield new ID[E](id)

  def extract[E <: IdEntity[E]](id: Option[ID[E]]): DBIO[Option[E]] = id match {
    case Some(id) => id.entity.map(p => Some(p))
    case None => DBIO.successful(None)
  }
}
