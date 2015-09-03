package slicky.entity

import slicky.Slicky._
import driver.api._

abstract class IdEntity[IE <: IdEntity[IE]](meta: IdEntityMeta[IE])
  extends IdentEntity[ID, IE](meta) {
  self: IE =>
  def id: Option[ID]
  def id_=(i: Option[ID]): Unit
  override def ident: ID = id.get
}

abstract class IdEntityMeta[IE <: IdEntity[IE]]
  extends IdentEntityMeta[ID, IE] {
  type IT = Table[IE] { def id: Rep[ID] }
  override def table: TableQuery[_ <: IT]
  override def byIdentQuery(ident: ID): Query[IT, IE, Seq] = table.filter(_.id === ident)
}
