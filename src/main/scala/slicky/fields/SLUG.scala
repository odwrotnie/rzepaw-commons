package slicky.fields

import commons.reflection.Spiegel
import slicky.Slicky._
import driver.api._
import slicky.entity._
import scala.concurrent.Future
import scala.reflect.runtime.universe._
import scala.util

trait AnySLUG {
  def value: String
  def meta: Option[AnySlugEntityMeta]
}

class SLUG[E <: SlugEntity[E]](val value: String)(implicit tag: TypeTag[E])
  extends MappedTo[String]
    with AnySLUG {

  def meta: Option[SlugEntityMeta[E]] = util.Try(Spiegel.companion[E].asInstanceOf[SlugEntityMeta[E]]).toOption

  lazy val entityOption: Option[DBIO[E]] = meta.map(_.bySlugGet(this))
  lazy val entity: DBIO[E] = entityOption.get

  override def equals(obj: scala.Any): Boolean = value.equals(obj)
  override def hashCode(): Int = value.hashCode

  override def toString = value.toString
}

object SLUG {

  def apply[E <: SlugEntity[E]](slug: String)(implicit tag: TypeTag[E]): SLUG[E] = new SLUG[E](slug)
  def apply[E <: SlugEntity[E]](any: { def slug: String })(implicit tag: TypeTag[E]): SLUG[E] = new SLUG[E](any.slug)
  def apply[E <: SlugEntity[E]](any: { def slug: Option[String] })(implicit tag: TypeTag[E]): Option[SLUG[E]] = any.slug.map(s => new SLUG[E](s))
  // def apply[E <: SlugEntity[E]](any: Option[{ def slug: String }])(implicit tag: TypeTag[E]): Option[SLUG[E]] = any.map(any => new SLUG[E](any.slug))
  def apply[E <: SlugEntity[E]](any: Option[{ def slug: Option[String] }])(implicit tag: TypeTag[E]): Option[SLUG[E]] = for {a <- any; id <- a.slug} yield new SLUG[E](id)

  def extract[E <: SlugEntity[E]](slug: Option[SLUG[E]]): DBIO[Option[E]] = slug match {
    case Some(slug) => slug.entity.map(p => Some(p))
    case None => DBIO.successful(None)
  }
}
