package commons.data

import collection.mutable._

case class MultiMap[T, V](value: Option[V],
                          m: Map[T, MultiMap[T, V]],
                          parent: Option[MultiMap[T, V]] = None) {

  private def level: Int = parent match {
    case None => 0
    case Some(p) => p.level + 1
  }

  def insert(ts: T*)(v: V): Unit = ts.toList match {
    case Nil =>
      throw new Exception(s"No keys specified")
    case t :: Nil =>
      m.get(t) match {
        case None =>
          m += t -> MultiMap(Some(v), Map[T, MultiMap[T, V]](), Some(this))
        case Some(_) =>
          throw new Exception(s"Key `$t` already exists")
      }
    case t :: tail =>
      m.get(t) match {
        case None =>
          val mm = MultiMap(None, Map[T, MultiMap[T, V]](), Some(this))
          m += t -> mm
          mm.insert(tail:_*)(v)
        case Some(mm) =>
          mm.insert(tail:_*)(v)
      }
  }

  override def toString = s"${ value.getOrElse("") }\n" + (m.map {
    case (t, mm) => s"${ "  " * level }$t: $mm"
  } mkString "")
}

object MultiMap {
  def empty[T, V](): MultiMap[T, V] = MultiMap[T, V](None, Map[T, MultiMap[T, V]]())
  def apply[T, V](value: V): MultiMap[T, V] = apply(Some(value), Map[T, MultiMap[T, V]]())
  def apply[T, V](tuples: Tuple2[T, V]*): MultiMap[T, V] = {
    val mmmap = MultiMap[T, V](None, Map[T, MultiMap[T, V]]())
    tuples foreach {
      case (k, v) => mmmap.insert(k)(v)
    }
    mmmap
  }
}
