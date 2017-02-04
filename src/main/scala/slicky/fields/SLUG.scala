//package slicky.fields
//
//import commons.reflection.Spiegel
//import slicky.Slicky._
//import driver.api._
//import slicky.entity._
//import scala.concurrent.Future
//import scala.reflect.runtime.universe._
//import scala.util
//
//trait AnySLUG {
//  def value: String
//  def meta: Option[AnyIdEntityMeta]
//}
//
//case class SLUG[E <: SlugEntity[E]](value: Long)(implicit tag: TypeTag[E])
//  extends MappedTo[Long]
//    with AnyID {
//
//  def meta: Option[SlugEntityMeta[E]] = util.Try(Spiegel.companion[E].asInstanceOf[SlugEntityMeta[E]]).toOption
//
//  lazy val entityOption: Option[DBIO[E]] = meta.map(_.bySlugGet(this))
//  lazy val entity: DBIO[E] = entityOption.get
//
//  override def toString = value.toString
//}
//
//object SLUG {
//  def apply[E <: IdEntity[E]](any: { def id: Long })(implicit tag: TypeTag[E]): SLUG[E] =
//    SLUG[E](any.id)
//  def apply[E <: IdEntity[E]](any: { def id: Option[Long] })(implicit tag: TypeTag[E]): Option[SLUG[E]] =
//    any.id.map(id => SLUG[E](id))
//  //  def apply[E <: IdEntity[E]](any: Option[{ def id: Long }])(implicit tag: TypeTag[E]): Option[SLUG[E]] =
//  //    any.map(any => SLUG[E](any.id))
//  def apply[E <: IdEntity[E]](any: Option[{ def id: Option[Long] }])(implicit tag: TypeTag[E]): Option[SLUG[E]] =
//    for {a <- any; id <- a.id} yield SLUG[E](id)
//
//  def extract[E <: IdEntity[E]](id: Option[SLUG[E]]): DBIO[Option[E]] = id match {
//    case Some(id) => id.entity.map(p => Some(p))
//    case None => DBIO.successful(None)
//  }
//}
