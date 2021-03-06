package slicky.entity

import commons.logger.Logger
import slicky.Slicky._
import driver.api._
import scala.concurrent.Future

/**
  * Entity identified by set of fields - IDENT
  *
  * @param meta
  * @tparam IDENT set of fields, i.e. (Int, String)
  * @tparam IE
  */
abstract class IdentEntity[IDENT, IE <: IdentEntity[IDENT, IE]](override val meta: IdentEntityMeta[IDENT, IE])
  extends Entity[IE](meta)
    with AnyIdentEntity {

  self: IE =>

  def ident: IDENT
  def save: DBIO[IE] = meta.save(this)
  def update: DBIO[IE] = meta.update(this)
  def delete: DBIO[IE] = meta.delete(this)
  def getOrInsert: DBIO[IE] = getOrInsert(meta.byIdentQuery(ident))
  def updateOrInsert: DBIO[IE] = updateOrInsert(meta.byIdentQuery(ident))
  def readFromDB: DBIO[Option[IE]] = meta.byIdent(ident)
}

abstract class IdentEntityMeta[IDENT, IE <: IdentEntity[IDENT, IE]]
  extends EntityMeta[IE]
    with AnyIdentEntityMeta
    with Logger {

  def byIdentQuery(ident: IDENT): Query[EntityTable, IE, Seq]

  def byIdent(ident: IDENT): DBIO[Option[IE]] = byIdentQuery(ident).result.headOption
  def byIdent(ident: Option[IDENT]): DBIO[Option[IE]] = ident match {
    case Some(ident) => byIdent(ident)
    case None => DBIO.successful(None)
  }

  def byIdentGet(ident: IDENT): DBIO[IE] = byIdent(ident).flatMap {
    case Some(ie) => DBIO.successful(ie)
    case _ => DBIO.failed(new Exception(s"There is no entity ${ getClass.getSimpleName } with ident: $ident"))
  }
  def byIdentGet(ident: Option[IDENT]): DBIO[Option[IE]] = ident match {
    case Some(ident) => byIdentGet(ident).map(Option.apply)
    case None => DBIO.successful(None)
  }

  //  def byIdentOptionOrCreate(ident: Option[IDENT], create: => Future[IE]): Future[IE] =
  //    byIdent(ident).flatMap(_.fold(create)(Future.successful))

  override def insert(ie: IE): DBIO[IE] = {
    val newE = beforeInsert(ie)
    (table += newE) map { rows =>
      require(rows == 1)
      newE
    }
  } map { e =>
    afterInsert(e)
    e
  }

  def update(ident: IDENT, ie: IE): DBIO[IE] =
    update(byIdentQuery(ident), ie)

  def update(query: Query[EntityTable, IE, Seq], ie: IE): DBIO[IE] = {
    val newIE = beforeUpdate(ie)
    query.update(newIE) map { rows =>
      require(rows == 1, s"IdentEntity update query in ${ getClass.getSimpleName } should return exactly 1 row: " +
        s"${ query.result.await } you should probably use save method instead")
      afterUpdate(newIE)
      newIE
    }
  }

  def update(ie: IE): DBIO[IE] = update(ie.ident, ie)

  def getByIdentOrInsert(ie: IE): DBIO[IE] = {
    val query = byIdentQuery(ie.ident)
    query.length.result.flatMap {
      case i if i == 0 => insert(ie)
      case i if i == 1 => query.result.head
      case _ => DBIO.failed(new Exception(s"Get or insert ${ getClass.getSimpleName } by ident ${ ie.ident } returned more than 1 row"))
    }
  }

  def save(ie: IE): DBIO[IE] = byIdentQuery(ie.ident).length.result.flatMap {
    case i if i == 0 => insert(ie)
    case i if i == 1 => update(ie)
    case _ => DBIO.failed(new Exception(s"Get or insert ${ getClass.getSimpleName } by ident ${ ie.ident } query returned more than 1 row"))
  }

  def delete(ie: IE): DBIO[IE] = {
    val newIE = beforeDelete(ie)
    byIdentQuery(ie.ident).delete map { rows =>
      require(rows == 1, s"There are $rows for $ie in delete")
      afterDelete(newIE)
      newIE
    }
  }

  // BEFORE
  def beforeSave(e: IE): IE = e
  def beforeUpdate(e: IE): IE = e
  def beforeDelete(e: IE): IE = e

  // AFTER
  def afterSave(e: IE): Unit = {
    debug(s"Saved (${ getClass.getSimpleName.replace("$", "") }): $e")
  }
  def afterUpdate(e: IE): Unit = Unit
  def afterDelete(e: IE): Unit = Unit
}
