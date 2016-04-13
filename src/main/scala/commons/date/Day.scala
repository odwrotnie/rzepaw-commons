package commons.date

import java.time.ZoneId

import de.jollyday.{HolidayCalendar, HolidayManager}
import org.joda.time._

object Day extends DateIntervalMeta[Day] {
  val WORKTIME_START_HOUR = 8
  val WORKTIME_END_HOUR = 16
  val DAYTIME_START_HOUR = 6
  val DAYTIME_END_HOUR = 22

  def daytimeIntervals(intervals: Interval*): Seq[Interval] = {
    if (intervals.isEmpty) Seq[Interval]() else {
      val sorted = intervals.sortBy(_.getStart.getMillis)
      val days: Stream[Day] =  surrounding(sorted.head.getStart, sorted.reverse.head.getEnd)
      val daytimes = days.map(_.daytimeInterval)
      val overlaps = for {
        interval <- intervals
        daytime <- daytimes
      } yield Option(interval.overlap(daytime))
      overlaps.flatten
    }
  }
  def daytimeDuration(intervals: Interval*): Duration =
    daytimeIntervals(intervals:_*).foldLeft(new Duration(0))((d, i) => d plus i.toDuration)
  def daytimeDurationHours(intervals: Interval*): Float =
    DateUtil.hours(daytimeDuration(intervals:_*))

  def holidayManager = HolidayManager.getInstance(HolidayCalendar.POLAND)
}

case class Day(dt: DateTime)
  extends DateInterval[Day](Day) {
  lazy val start = dt.withTimeAtStartOfDay
  lazy val end = start plusDays 1

  lazy val worktimeInterval: Interval = new Interval(start.withHourOfDay(Day.WORKTIME_START_HOUR), start.withHourOfDay(Day.WORKTIME_END_HOUR))
  lazy val worktimeHours: List[Hour] = Hour.surrounding(worktimeInterval).toList
  lazy val daytimeInterval: Interval = new Interval(start.withHourOfDay(Day.DAYTIME_START_HOUR), start.withHourOfDay(Day.DAYTIME_END_HOUR))
  lazy val daytimeHours: List[Hour] = Hour.surrounding(daytimeInterval).toList

  def dayOfWeek = start.getDayOfWeek // Mon == 1, Tue == 2... Sun == 7
  def dayOfMonth = start.getDayOfMonth
  def month = Month(start)
  def firstHour = Hour(start)
  def hours = firstHour.nextInclusive(24)

  def isHoliday =
    (dayOfWeek >= 6) ||
      Day.holidayManager.isHoliday(start.toDate.toInstant.atZone(ZoneId.systemDefault()).toLocalDate)

  override def toString = "%s - %s/%s/%s" format (start.dayOfWeek.getAsText,
    start.getYear, start.getMonthOfYear, start.getDayOfMonth)
}
