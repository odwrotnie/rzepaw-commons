package slicky.entity

import commons.logger.Logger
import slicky.Slicky._
import driver.api._
import scala.concurrent.Future

/**
 * Entity identified by set of fields - IDENT
 * @param meta
 * @tparam IDENT set of fields, i.e. (Int, String)
 * @tparam IE
 */
abstract class IdentEntity[IDENT, IE <: IdentEntity[IDENT, IE]](override val meta: IdentEntityMeta[IDENT, IE])
  extends Entity[IE](meta) {
  self: IE =>
  def ident: IDENT
  def save: Future[IE] = meta.save(this)
  def update: Future[IE] = meta.update(this)
  def delete: Future[IE] = meta.delete(this)
}

abstract class IdentEntityMeta[IDENT, IE <: IdentEntity[IDENT, IE]]
  extends EntityMeta[IE]
  with Logger {

  def byIdentQuery(ident: IDENT): Query[T, IE, Seq]
  def byIdent(ident: IDENT): Future[Option[IE]] = dbFuture { byIdentQuery(ident).result.headOption }
  def byIdent(ident: Option[IDENT]): Future[Option[IE]] = ident match {
    case Some(ident) => byIdent(ident)
    case None => Future.successful(None)
  }
  def byIdentGet(ident: IDENT): Future[IE] = byIdent(ident).map(_.get)
//  def byIdentOptionOrCreate(ident: Option[IDENT], create: => Future[IE]): Future[IE] =
//    byIdent(ident).flatMap(_.fold(create)(Future.successful))

  override def insert(ie: IE): Future[IE] = dbFuture {
    val newE = beforeInsert(ie)
    (table += newE).named(s"Insert $ie") map { rows =>
      require(rows == 1)
      newE
    }
  } map { e =>
    afterInsert(e)
    e
  }

  def update(ident: IDENT, ie: IE): Future[IE] = dbFuture {
    val newIE = beforeUpdate(ie)
    byIdentQuery(ident).update(newIE) map { rows =>
      require(rows == 1)
      afterUpdate(newIE)
      newIE
    }
  }

  def update(ie: IE): Future[IE] = update(ie.ident, ie)

  def save(ie: IE): Future[IE] = {
    val newIE = beforeSave(ie)
    byIdent(ie.ident).flatMap {
      case Some(ie) => update(ie.ident, newIE).map { _ =>
        afterSave(newIE)
        newIE
      }
      case None => insert(newIE).map { _ =>
        afterSave(newIE)
        newIE
      }
    }
  }

  def delete(ie: IE): Future[IE] = dbFuture {
    val newIE = beforeDelete(ie)
    byIdentQuery(ie.ident).delete map { rows =>
      require(rows == 1)
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
    debug(s"Saved (${ getClass.getSimpleName }): $e")
  }
  def afterUpdate(e: IE): Unit = Unit
  def afterDelete(e: IE): Unit = Unit
}
