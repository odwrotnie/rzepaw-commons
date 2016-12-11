package commons.data

abstract class Statistics[T](list: Seq[T],
                             labels: String*) {

  def value(t: T)(label: String): Double

  // BY LABEL

  def values(label: String): Seq[Double] = list.map { t =>
    value(t)(label)
  }

  def stats(label: String): DescStats = {
    DescStats(values(label))
  }

  // BY T

  def valuesMap(t: T): Map[String, Double] = labels.map { label =>
    label -> value(t)(label)
  }.toMap

  // WHOLE

  lazy val valuesMap: Map[String, Seq[Double]] = labels.map { label =>
    label -> values(label)
  }.toMap

  lazy val statsMap: Map[String, DescStats] = labels.map { label =>
    label -> stats(label)
  }.toMap

  lazy val aggregateTable = new AggregateTable[String, T, Double](labels, list) {
    override def _agg(label: String, t: T): Double = value(t)(label)
  }
}
