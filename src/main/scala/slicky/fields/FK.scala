package slicky.fields

import slicky.Slicky._

abstract class FK[E <: { def id: Option[ID] }](val e: E) {
  def id = e.id
}

//case class CompanyFK
//  extends FK[Company]
