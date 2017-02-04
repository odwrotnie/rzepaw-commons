//package slicky.entity
//
//import slicky.Slicky._
//import driver.api._
//import slicky.fields.SLUG
//import scala.reflect.runtime.universe._
//
//abstract class SlugEntity[IE <: SlugEntity[IE]](override val meta: SlugEntityMeta[IE])
//  extends IdentEntity[SLUG[IE], IE](meta) {
//
//  self: IE =>
//
//  def id: Option[SLUG[IE]]
//  def withId(id: Option[SLUG[IE]]): IE
//  override def ident: SLUG[IE] = id match {
//    case Some(id) => id
//    case _ => throw new Exception(s"Entity $this has no id yet")
//  }
//
//  override def getOrInsert(query: Query[_, IE, Seq]): DBIO[IE] = meta.getOrInsert(query, this)
//  override def updateOrInsert(query: Query[_, IE, Seq]): DBIO[IE] = meta.updateOrInsert(query, this)
//}
//
//abstract class SlugEntityMeta[IE <: SlugEntity[IE]](implicit tag: TypeTag[IE])
//  extends SlugEntityMeta[SLUG[IE], IE] {
//
//  abstract class EntityTableWithId(tag: Tag) extends EntityTable(tag) { def id: Rep[SLUG[IE]] }
//
//  override def table: TableQuery[_ <: EntityTableWithId]
//  override def byIdentQuery(ident: SLUG[IE]): Query[EntityTableWithId, IE, Seq] = table.filter(_.id === ident)
//
//  override def insert(ie: IE): DBIO[IE] = {
//    require(ie.id.isEmpty, s"Inserting entity $ie with defined id: ${ ie.ident }")
//    val newIE = beforeInsert(ie)
//    val idAction = (table returning table.map(_.id)) += ie
//    idAction.map { id: SLUG[IE] =>
//      val withId = newIE.withId(Some(id))
//      afterInsert(withId)
//      withId
//    }
//  }
//
//  override def save(ie: IE): DBIO[IE] = {
//    val newIE = beforeSave(ie)
//    ie.id match {
//      case Some(id) => update(ie.ident, newIE).map { _ =>
//        afterSave(newIE)
//        newIE
//      }
//      case _ => insert(newIE).map { withId =>
//        afterSave(withId)
//        withId
//      }
//    }
//  }
//
//  def byId(id: SLUG[IE]): DBIO[Option[IE]] = super.byIdent(id)
//  def byIdGet(id: SLUG[IE]): DBIO[IE] = super.byIdentGet(id)
//  def byId(id: Option[SLUG[IE]]): DBIO[Option[IE]] = super.byIdent(id)
//  def byIdGet(id: Option[SLUG[IE]]): DBIO[Option[IE]] = super.byIdentGet(id)
//
//  def byIdentNumber(id: Long): DBIO[Option[IE]] = super.byIdent(SLUG[IE](id))
//  def byIdentNumberGet(id: Long): DBIO[IE] = super.byIdentGet(SLUG[IE](id))
//  def byIdentNumber(id: Option[Long]): DBIO[Option[IE]] = super.byIdent(id.map(l => SLUG[IE](l)))
//  def byIdentNumberGet(id: Option[Long]): DBIO[Option[IE]] = super.byIdentGet(id.map(l => SLUG[IE](l)))
//
//  //  private def cleanId(e: IE): IE = {
//  //    val clone = e
//  //    clone.getClass.getMethods.find(_.getName == "id_$eq").get.invoke(clone, None)
//  //    clone
//  //  }
//
//  override def getOrInsert(query: Query[_, IE, Seq], e: IE): DBIO[IE] = {
//    query.length.result.flatMap {
//      case i if i == 0 => insert(e.withId(None))
//      case i if i == 1 => super.getOrInsert(query, e)
//    }
//  }
//
//  override def updateOrInsert(query: Query[_, IE, Seq], e: IE): DBIO[IE] = {
//    query.length.result.flatMap {
//      case i if i == 0 => insert(e.withId(None))
//      case i if i == 1 => query.result.head flatMap { fromDb => update(e.withId(fromDb.id)) }
//      case i => super.updateOrInsert(query, e)
//    }
//  }
//}
