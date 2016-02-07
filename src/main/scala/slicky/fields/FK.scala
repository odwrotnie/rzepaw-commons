package slicky.fields

import slicky.Slicky._
import slicky.entity.{IdEntityMeta, IdEntity}

abstract class FK[E <: IdEntity[E]](var e: Option[E], var ident: Option[ID] = None) {
  (e, ident) match {
    case (Some(e), None) => ident = e.id
    case (None, Some(i)) => e = meta.byIdent(i).await
  }
  def meta: IdEntityMeta[E]
}

//case class CompanyFK
//  extends FK[Company]
