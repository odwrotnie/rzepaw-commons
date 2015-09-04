package slicky.entity

import slicky.Slicky._
import driver.api._
import scala.concurrent.Future

/**
 * Entity identified by set of fields - IDENT
 * @param meta
 * @tparam IDENT set of fields, i.e. (Int, String)
 * @tparam IE
 */
abstract class IdentEntity[IDENT, IE <: IdentEntity[IDENT, IE]](meta: IdentEntityMeta[IDENT, IE])
  extends Entity[IE](meta) {
  self: IE =>
  def ident: IDENT
  def save: Future[IE] = meta.save(this)
  def update: Future[IE] = meta.update(this)
  def delete: Future[IE] = meta.delete(this)
}

abstract class IdentEntityMeta[IDENT, IE <: IdentEntity[IDENT, IE]]
  extends EntityMeta[IE] {

  def byIdentQuery(ident: IDENT): Query[T, IE, Seq]
  def byIdent(ident: IDENT): Future[Option[IE]] = dbFuture { byIdentQuery(ident).result.headOption }

  override def insert(ie: IE): Future[IE] = dbFuture {
    val newE = beforeInsert(ie)
    (table += newE).named(s"Insert $ie") map { rows =>
      require(rows == 1)
      newE
    }
  } map { e =>
    afterInsert(e)
  }

  def update(ident: IDENT, ie: IE): Future[IE] = dbFuture {
    val newIE = beforeUpdate(ie)
    byIdentQuery(ident).update(newIE) map { rows =>
      require(rows == 1)
      afterUpdate(newIE)
    }
  }

  def update(ie: IE): Future[IE] = update(ie.ident, ie)

  def save(ie: IE): Future[IE] = {
    val newIE = beforeSave(ie)
    byIdent(ie.ident).flatMap {
      case Some(ie) => update(ie.ident, newIE).map(_ => afterSave(newIE))
      case None => insert(newIE).map(_ => afterSave(newIE))
    }
  }

  def delete(ie: IE): Future[IE] = dbFuture {
    val newIE = beforeDelete(ie)
    byIdentQuery(ie.ident).delete map { rows =>
      require(rows == 1)
      afterDelete(newIE)
    }
  }

  // BEFORE
  def beforeSave(e: IE): IE = e
  def beforeUpdate(e: IE): IE = e
  def beforeDelete(e: IE): IE = e

  // AFTER
  def afterSave(e: IE): IE = e
  def afterUpdate(e: IE): IE = e
  def afterDelete(e: IE): IE = e
}
