package commons.data

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics

case class DescStats(values: Iterable[Double]) {

  lazy val statistics = new DescriptiveStatistics(values.toArray)

  lazy val mean: Double = statistics.getMean
  lazy val sum: Double = statistics.getSum
  lazy val stdDev: Double = statistics.getStandardDeviation
  lazy val coeffOfVariation: Double = stdDev / mean // Coefficient of variation
  lazy val variance: Double = statistics.getPopulationVariance
  lazy val min: Double = statistics.getMin
  lazy val max: Double = statistics.getMax

  lazy val kurtosis: Double = statistics.getKurtosis
  lazy val skewness: Double = statistics.getSkewness

  lazy val q1: Double = statistics.getPercentile(25)
  lazy val q2: Double = statistics.getPercentile(50)
  lazy val q3: Double = statistics.getPercentile(75)
  lazy val iqr: Double = q3 - q1 // interquartile range

  // For box plot - https://en.wikipedia.org/wiki/Box_plot
  lazy val whiskerLow: Double = q1 - 1.5 * iqr
  lazy val whiskerHigh: Double = q3 + 1.5 * iqr
  lazy val outliers: Iterable[Double] = values.filter(o => o < whiskerLow || o > whiskerHigh)

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
