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

  def updateOrInsert(query: Query[meta.T, IE, Seq]): Future[IE] = meta.updateOrInsert(query, this)
}

abstract class IdentEntityMeta[IDENT, IE <: IdentEntity[IDENT, IE]]
  extends EntityMeta[IE]
  with Logger {

  def byIdentQuery(ident: IDENT): Query[T, IE, Seq]

  def byIdent(ident: IDENT): Future[Option[IE]] = dbFuture {
    byIdentQuery(ident).result.headOption
  }

  def byIdent(ident: Option[IDENT]): Future[Option[IE]] = ident match {
    case Some(ident) => byIdent(ident)
    case None => Future.successful(None)
  }

  def byIdentGet(ident: IDENT): Future[IE] = byIdent(ident).map {
    case Some(ie) => ie
    case _ => throw new Exception(s"There is no entity ${ getClass.getSimpleName } with ident: $ident")
  }

  //  def byIdentOptionOrCreate(ident: Option[IDENT], create: => Future[IE]): Future[IE] =
  //    byIdent(ident).flatMap(_.fold(create)(Future.successful))

  override def insert(ie: IE): Future[IE] = dbFuture {
    val newE = beforeInsert(ie)
    (table += newE) map { rows =>
      require(rows == 1)
      newE
    }
  } map { e =>
    afterInsert(e)
    e
  }

  def update(ident: IDENT, ie: IE): Future[IE] =
    update(byIdentQuery(ident), ie)

  def update(query: Query[T, IE, Seq], ie: IE): Future[IE] = dbFuture {
    val newIE = beforeUpdate(ie)
    query.update(newIE) map { rows =>
      require(rows == 1)
      afterUpdate(newIE)
      newIE
    }
  }

  def update(ie: IE): Future[IE] = update(ie.ident, ie)

  def updateOrInsert(query: Query[T, IE, Seq], ie: IE): Future[IE] = {
    dbFuture {
      query.length.result
    } flatMap {
      case i if i == 0 => insert(ie)
      case i if i == 1 => dbFuture(query.update(ie)).flatMap { _ => dbFuture(query.result.head) }
      case _ => Future.failed(new Exception("The query returned more than 1 row"))
    }
  }

  def save(ie: IE): Future[IE] = updateOrInsert(byIdentQuery(ie.ident), ie)

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
    debug(s"Saved (${ getClass.getSimpleName.replace("$", "") }): $e")
  }
  def afterUpdate(e: IE): Unit = Unit
  def afterDelete(e: IE): Unit = Unit
}
