package commons.date

import org.joda.time.DateTime

object Minute extends DateIntervalMeta[Minute]

case class Minute(dt: DateTime)
  extends DateInterval[Minute](Minute) {
  lazy val start = dt.withSecondOfMinute(0).withMillisOfSecond(0)
  lazy val end = start plusMinutes 1

  override def toString = DateUtil.formatHour(start)
}
