package commons.statistics

case class Ratio(fraction: Float, whole: Float) {

  def percent = {
    val f: Float = (fraction, whole) match {
      case (a, b) if ((a <= 0) || (b < 0)) => 0f
      case (_, 0) => 1f
      case _ => math.min(1, fraction / whole)
    }
    f * 100
  }

  def percentRound = math.round(percent)

  def percentString = "%.0f%%".format(percent)
}
