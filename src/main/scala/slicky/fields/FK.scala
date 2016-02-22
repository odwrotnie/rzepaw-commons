package slicky.fields

import commons.reflection.Spiegel
import slicky.Slicky._
import driver.api._
import slicky.entity._
import scala.concurrent.Future
import scala.reflect.runtime.universe._

case class FK[E <: IdEntity[E]](id: Option[ID])(implicit tag: TypeTag[E]) {
  lazy val meta = Spiegel.companion[E].asInstanceOf[IdEntityMeta[E]]
  lazy val entity: Future[Option[E]] = meta.byIdent(id).future
  override def toString = List(id, entity.await).flatten.mkString(" => ")
}

object FK {
  def mapper[E <: IdEntity[E]](implicit tag: TypeTag[E]) = {
    MappedColumnType.base[FK[E], ID](
      fk => fk.id.get,
      id => FK[E](Some(id))
    )
  }
}
