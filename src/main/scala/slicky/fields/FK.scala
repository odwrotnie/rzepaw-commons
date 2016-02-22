package slicky.fields

import slicky.Slicky._
import driver.api._
import slicky.entity._

abstract class ForeignKey[E <: IdEntity[E]] {
  def meta: IdEntityMeta[E]
  def entity: Option[E]
  def entity_=(entity: Option[E]): Unit
  def id: Option[ID]
  def id_=(id: Option[ID]): Unit
  (entity, id) match {
    case (Some(e), None) => id = e.id
    case (None, Some(i)) => entity = meta.byIdent(i).await
  }
}

object FK {
  import scala.reflect.ClassTag
  def mapper[F: ClassTag](create: (ID) => F, id: (F) => ID) = MappedColumnType.base[F, ID](
    fk => id(fk),
    id => create(id)
  )
}
