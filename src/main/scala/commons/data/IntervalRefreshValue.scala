package commons.data

import commons.date.{DateInterval, Day, DateUtil}
import commons.logger.Logger
import commons.statistics.Timed
import org.joda.time.DateTime

class IntervalRefreshValue[T](retrieve: => T, intervalHours: Float = 3f)
  extends Logger {

  private var (value, lastTimeRefreshed) = refresh
  private def refresh: (T, Long) = (retrieve, DateUtil.now.getMillis)

  def get: T = if ((DateUtil.now.getMillis - lastTimeRefreshed) < (intervalHours * DateUtil.MILLISECONDS_IN_HOUR)) {
    value
  } else {
    val timed = Timed("Retrieve")
    val r = timed.log((retrieve, DateUtil.now.getMillis))
    value = r._1
    lastTimeRefreshed = r._2
    value
  }

  override def toString = get.toString
}
