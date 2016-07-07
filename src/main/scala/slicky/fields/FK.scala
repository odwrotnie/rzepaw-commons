package slicky.fields

import commons.reflection.Spiegel
import slicky.Slicky._
import driver.api._
import slicky.entity._
import scala.concurrent.Future
import scala.reflect.runtime.universe._

case class FK[E <: IdEntity[E]](id: ID)(implicit tag: TypeTag[E]) extends MappedTo[Long] {

  override def value: ID = id

  lazy val meta = Spiegel.companion[E].asInstanceOf[IdEntityMeta[E]]
  lazy val entity: Future[E] = meta.byIdentGet(value).future

  override def toString = List(value, entity.await).mkString(" => ")
}

object FK {

  def apply[E <: IdEntity[E]](entity: E)(implicit tag: TypeTag[E]): FK[E] = {
    require(entity.id.isDefined, s"Id should be defined for $entity")
    FK[E](entity.ident)
  }
}
