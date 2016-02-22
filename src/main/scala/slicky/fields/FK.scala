package slicky.fields

import commons.reflection.Spiegel
import slicky.Slicky._
import driver.api._
import slicky.entity._
import scala.concurrent.Future
import scala.reflect.runtime.universe._

case class FK[E <: IdEntity[E]](id: ID)(implicit tag: TypeTag[E]) {
  lazy val meta = Spiegel.companion[E].asInstanceOf[IdEntityMeta[E]]
  lazy val entity: Future[E] = meta.byIdentGet(id).future
  override def toString = List(id, entity.await).mkString(" => ")
}

object FK {
  def apply[E <: IdEntity[E]](entity: E)(implicit tag: TypeTag[E]): FK[E] =
    FK[E](entity.ident)
  def mapper[E <: IdEntity[E]](implicit tag: TypeTag[E]) = {
    MappedColumnType.base[FK[E], ID](
      fk => fk.id,
      id => FK[E](id)
    )
  }
}
