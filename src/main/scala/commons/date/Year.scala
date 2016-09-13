package commons.date

import org.joda.time.DateTime

object Year
  extends DateIntervalMeta[Year] {

  def byNumber(ad: Int) = Year(DateUtil.date(ad, 1, 1))
}

case class Year(dt: DateTime)
  extends DateInterval[Year](Year) {
  lazy val start = dt.withDayOfYear(1).withTimeAtStartOfDay()
  lazy val end = start plusYears 1

  lazy val firstDay = Day(start)
  lazy val days: Stream[Day] = firstDay
    .nextInclusiveStream
    .takeWhile(d => d.start.isBefore(end))
  lazy val firstWeek = Week(start)
  lazy val weeks: Stream[Week] = firstWeek
    .nextInclusiveStream
    .takeWhile(w => w.start.isBefore(end))
  lazy val firstMonth = Month(start)
  lazy val months: Stream[Month] = firstMonth
    .nextInclusiveStream
    .takeWhile(m => m.start.isBefore(end))

  def month(number: Int): Month = months(number - 1)

  override def toString =
    "%s %s" format (start.monthOfYear.getAsShortText, start.yearOfEra.getAsText)
}
