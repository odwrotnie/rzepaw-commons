package commons.data

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics

case class DescStats(values: Iterable[Double]) {

  lazy val statistics = new DescriptiveStatistics(values.toArray)

  lazy val mean = statistics.getMean
  lazy val sum = statistics.getSum
  lazy val stdDev = statistics.getStandardDeviation
  lazy val variance = statistics.getPopulationVariance
  lazy val min = statistics.getMin
  lazy val max = statistics.getMax

  lazy val deciles: Map[Int, Double] = (1 to 9) map { i => i -> statistics.getPercentile(10 * i) } toMap
  lazy val decilesString: String =  (1 to 9) map { i => s"$i: ${statistics.getPercentile(10 * i)}" } mkString ", "

  override def toString: String = {

    s"""STATYSTYKI OPISOWE:
       | - Średnia arytmetyczna: $mean, suma: $sum
       | - Odchylenie standardowe: $stdDev
       | - Wartośc maksymalna: $max, minimalna: $min
       | - Decyle - $decilesString
       | - Wariancja populacji: $variance""".stripMargin
  }
}
