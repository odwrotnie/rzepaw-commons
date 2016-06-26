package commons.data

case class MultiMap[T, V](m: collection.mutable.Map[T, Either[MultiMap[T, V], V]],
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
          m += t -> Right(v)
        case Some(_) =>
          throw new Exception(s"Key `$t` already exists")
      }
    case t :: tail =>
      m.get(t) match {
        case None =>
          val mmm = MultiMap(collection.mutable.Map[T, Either[MultiMap[T, V], V]](), Some(this))
          m += t -> Left(mmm)
          mmm.insert(tail:_*)(v)
        case Some(Left(mmm)) =>
          mmm.insert(tail:_*)(v)
        case Some(Right(v)) =>
          throw new Exception(s"Value `$v` already exists")
      }
  }

  override def toString = m.map {
    case (t, Left(m)) => s"+ ${ "  " * level }$t:\n$m"
    case (t, Right(a)) => s"- ${ "  " * level }$t: $a"
  } mkString "\n"
}

object MultiMap {
  def empty[T, V](): MultiMap[T, V] = MultiMap[T, V](collection.mutable.Map[T, Either[MultiMap[T, V], V]]())
  def apply[T, V](tuples: Tuple2[T, V]*): MultiMap[T, V] = {
    val mmmap = MultiMap[T, V](collection.mutable.Map[T, Either[MultiMap[T, V], V]]())
    tuples foreach {
      case (k, v) => mmmap.insert(k)(v)
    }
    mmmap
  }
}
