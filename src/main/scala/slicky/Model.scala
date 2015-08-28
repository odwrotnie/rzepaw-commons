package slicky

import commons.logger.Logger
import slick.lifted.TableQuery
import scala.concurrent.Future
import slicky.Slicky._
import driver.api._

abstract class Model[M <: Model[M]](val meta: MetaModel[M]) {

  def id: Option[ID]
  def id_=(i: Option[ID]): Unit
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
  def byId(id: ID): Future[Option[M]] = dbFuture {
    table.filter(_.id === id).result.headOption
  }

  // UPDATE
  def update(m: M): Future[Int] = dbFuture {
    require(m.id.isDefined)
    val i = table.filter(_.id === m.id.get).update(beforeUpdate(m))
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
}
