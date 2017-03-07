package commons.data

trait Statistics[T] {

  val list: Seq[T]
  val labels: Map[String, T => Double]
  def value(t: T)(label: String): Double = labels.get(label).get(t)

  // BY LABEL

  def values(label: String): Seq[Double] = list.map { t =>
    value(t)(label)
  }

  def stats(label: String): DescStats = {
    DescStats(values(label))
  }

  // BY T

  def valuesMap(t: T): Map[String, Double] = labels.keys.map { label =>
    label -> value(t)(label)
  }.toMap

  // WHOLE

  lazy val nameMap: Map[String, T] = labels.keys.map { label =>
    label ->
  }.toMap

  lazy val valuesMap: Map[String, Seq[Double]] = labels.keys.map { label =>
    label -> values(label)
  }.toMap

  lazy val statsMap: Map[String, DescStats] = labels.keys.map { label =>
    label -> stats(label)
  }.toMap

  lazy val aggregateTable = new AggregateTable[String, T, Double](labels.keys, list) {
    override def _rowcol(label: String, t: T): Double = value(t)(label)
    override def _agg(cols: Iterable[Double]): Double = cols.sum
  }
}
