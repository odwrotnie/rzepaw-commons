package slicky

import commons.logger.Logger
import slick.lifted.TableQuery
import scala.concurrent.Future
import slicky.Slicky._
import driver.api._

// TODO Mdl i MdlID sa zrobione zajebiscie ale trzeba to przetestowac, potem MdlID zamienic na Model i usunac klase Model

abstract class MdlID[M <: MdlID[M]](meta: MetaMdlID[M])
  extends Mdl[ID, M](meta) {
  self: M =>

//  def ident: IDENT
//  def ident_=(i: Option[ID]): Unit
}

abstract class MetaMdlID[M <: MdlID[M]]
  extends MetaMdl[ID, M] {

  //def byIdQuery(ident: ID) =
}

abstract class Mdl[IDENT, M <: Mdl[IDENT, M]](val meta: MetaMdl[IDENT, M]) {

  this: M =>

  def ident: IDENT

  def save: Future[M] = meta.save(this)
  def update: Future[Int] = meta.update(this)
  def delete: Future[Int] = meta.delete(this)
}

abstract class MetaMdl[IDENT, M <: Mdl[IDENT, M]]
  extends Logger {

  type T = Table[M]
  def table: TableQuery[_ <: T]

  def byIdQuery(ident: IDENT): Query[T, M, Seq]
  def byId(ident: IDENT): Future[Option[M]] = dbFuture { byIdQuery(ident).result.headOption }

  def insert(m: M): Future[M] = dbFuture {
    (table += beforeSave(m)).named(s"Insert $m")
  }.map { _ =>
    afterSave(m)
  }

  def save(m: M): Future[M] = {
    beforeSave(m)
    byId(m.ident).flatMap {
      case Some(m) => update(m).map(_ => afterSave(m))
      case None => insert(m).map(_ => afterSave(m))
    }
  }

  def update(m: M): Future[Int] = dbFuture {
    val i = byIdQuery(m.ident).update(beforeUpdate(m))
    afterUpdate(m)
    i
  }

  def delete(m: M): Future[Int] = dbFuture {
    byIdQuery(m.ident).delete
  }

  // BEFORE
  def beforeInsert(m: M): M = m
  def beforeSave(m: M): M = m
  def beforeUpdate(m: M): M = m

  // AFTER
  def afterInsert(m: M): M = m
  def afterSave(m: M): M = {
    debug(s"Saved: $m")
    m
  }
  def afterUpdate(m: M): M = m
}

abstract class Model[M <: Model[M]](val meta: MetaModel[M]) {

  this: M =>

  def id: Option[ID]
  def id_=(i: Option[ID]): Unit

  def save: Future[M] = meta.save(this)
  def update: Future[Int] = meta.update(this)
  def delete: Future[Int] = meta.delete(this)
}

abstract class MetaModel[M <: Model[M]]
  extends Logger {

  type T = Table[M] { def id: Rep[ID] }
  def table: TableQuery[_ <: T]

  def insert(m: M): Future[M] = {
    require(m.id.isEmpty)
    val idFuture: Future[ID] = dbFuture((table returning table.map(_.id)) += beforeInsert(m))
    idFuture.map { id: ID =>
      m.id = Some(id)
      afterInsert(m)
    }
  }

  // CREATE
  def save(m: M): Future[M] = {
    beforeSave(m)
    m.id match {
      case Some(id) => update(m).map(_ => afterSave(m))
      case None => insert(m).map(_ => afterSave(m))
    }
  }
  def saveAwait(m: M): M = await(save(m))

  // READ
  def count: Int = dbAwait(table.length.result)
  def all: Stream[M] = streamify(table)
  def byIdQuery(id: ID): Query[T, M, Seq] = table.filter(_.id === id)
  def byId(id: ID): Future[Option[M]] = dbFuture { byIdQuery(id).result.headOption }

  // UPDATE
  def update(m: M): Future[Int] = dbFuture {
    require(m.id.isDefined)
    val i = byIdQuery(m.id.get).update(beforeUpdate(m))
    afterUpdate(m)
    i
  }

  // DELETE
  def delete(id: ID): Future[Int] = dbFuture {
    table.filter(_.id === id).delete
  }
  def delete(m: M): Future[Int] = {
    require(m.id.isDefined)
    delete(m.id.get)
  }

  // BEFORE
  def beforeInsert(m: M): M = m
  def beforeSave(m: M): M = m
  def beforeUpdate(m: M): M = m

  // AFTER
  def afterInsert(m: M): M = m
  def afterSave(m: M): M = {
    debug(s"Saved: $m")
    m
  }
  def afterUpdate(m: M): M = m

  def streamify[T](query: Query[T, M, Seq], pageSize: Int = 31): Stream[M] =
    Slicky.streamify[M](query, pageSize)
}
