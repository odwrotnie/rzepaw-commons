package commons.date

import org.joda.time.DateTime

object Month
  extends DateIntervalMeta[Month] {

  def apply(year: Int, month: Int): Month = Month(DateUtil.date(year, month, 1))
}

case class Month(dt: DateTime)
  extends DateInterval[Month](Month) {
  lazy val start = dt.withDayOfMonth(1).withTimeAtStartOfDay()
  lazy val end = start plusMonths 1

  lazy val daysCount: Int = dt.dayOfMonth().getMaximumValue

  lazy val monthOfYear = start.getMonthOfYear
  lazy val firstHour = Hour(start)
  lazy val firstDay = Day(start)
  lazy val firstWeek = Week(start)
  lazy val hours: Stream[Hour] = firstHour
    .nextInclusiveStream
    .takeWhile(d => d.start.isBefore(end))
  lazy val days: Stream[Day] = firstDay
    .nextInclusiveStream
    .takeWhile(d => d.start.isBefore(end))
  lazy val weeks: Stream[Week] = firstWeek
    .nextInclusiveStream
    .takeWhile(w => w.start.isBefore(end))

  override def toString =
    "%s %s" format (start.monthOfYear.getAsShortText, start.yearOfEra.getAsText)
}
