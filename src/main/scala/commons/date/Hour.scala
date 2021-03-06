package commons.date

import org.joda.time.DateTime

object Hour extends DateIntervalMeta[Hour] {
  def toFloat(dt: DateTime): Float = {
    val hour = Hour(dt)
    hour.start.getHourOfDay + (dt.getMillis - hour.startMillis).toFloat / hour.millis
  }
}

case class Hour(dt: DateTime)
  extends DateInterval[Hour](Hour) {

  lazy val start = dt.withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0)
  lazy val end = start plusHours 1

  override def toString = DateUtil.formatHour(start)
}
