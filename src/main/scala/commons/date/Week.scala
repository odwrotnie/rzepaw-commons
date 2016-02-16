package commons.date

import org.joda.time._

object Week extends DateIntervalMeta[Week]

case class Week(dt: DateTime)
  extends DateInterval[Week](Week) {
  lazy val start = dt.withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay()
  lazy val end = start plusWeeks 1

  lazy val firstDay = Day(start)
  def days = firstDay :: firstDay.next(6)
}