package commons.date

import java.sql.Timestamp

import commons.date.DateUtil.TimePlace
import commons.text._
import org.joda.time._
import org.joda.time.base._
import org.joda.time.format._
import org.ocpsoft.prettytime.PrettyTime

import scala.util.Try

object DateUtil {

  val DATE_FORMAT = "dd/MM/YYYY"
  val HOUR_FORMAT = "HH:mm"
  val TIME_FORMAT = s"$DATE_FORMAT $HOUR_FORMAT"

  val DATE_PATTERN = DateTimeFormat.forPattern(DATE_FORMAT)
  val HOUR_PATTERN = DateTimeFormat.forPattern(HOUR_FORMAT)
  val TIME_PATTERN = DateTimeFormat.forPattern(TIME_FORMAT)

  val timeFormatStringJQuery = "hh:mm"

  def now: DateTime = new DateTime()
  def date(y: Int, m: Int, d: Int): DateTime = new LocalDate(y, m, d).toDateTimeAtStartOfDay
  def nowDateOnly: DateTime = now.withTimeAtStartOfDay
  def nowString = formatTime(now)
  def todayString = formatDate(now)
  private val pt = new PrettyTime
  def humanReadable(date: AbstractInstant) = pt.format(date.toDate)

  def format(date: AbstractInstant, format: String): String = DateTimeFormat.forPattern(format).print(date)

  def formatHour(date: AbstractInstant): String = HOUR_PATTERN.print(date)
  def formatHour(date: java.util.Date): String = formatHour(new DateTime(date))
  def formatDate(date: AbstractInstant): String = DATE_PATTERN.print(date)
  def formatDate(date: java.util.Date): String = formatDate(new DateTime(date))
  def formatTime(date: AbstractInstant): String = TIME_PATTERN.print(date)
  def formatTime(date: java.util.Date): String = formatTime(new DateTime(date))

  def formatOnlyHour(date: AbstractInstant): String = HOUR_PATTERN.print(date)

  def getDate(l: Long): DateTime = new DateTime(new java.util.Date(l))
  def parseDate(s: String): Option[DateTime] = ParserChain[DateTime](s.trim.replaceAll("[^\\d]", "/")) +
    (s => { DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(s).withTimeAtStartOfDay }) +
    (s => { DateTimeFormat.forPattern("YYYY/MM/dd").parseDateTime(s) }) parse
  def parseDateTime(s: String): Option[DateTime] = Try(TIME_PATTERN.parseDateTime(s)).toOption

  def daysBetween(start: AbstractInstant, end: AbstractInstant) = Days.daysBetween(start, end).getDays()
  def daysTill(date: AbstractInstant) = daysBetween(now, date)

  def getDayNumber(date: AbstractInstant) = date.toDateTime().getDayOfMonth()
  def getMonthName(date: AbstractInstant) = DateTimeFormat.forPattern("MMMM").print(date)
  def getYear(date: AbstractInstant) = DateTimeFormat.forPattern("yyyy").print(date)

  implicit def dateTimeOrdering: Ordering[DateTime] = Ordering.fromLessThan(_ isBefore _)
  def ascending(dates: DateTime*): Seq[DateTime] = dates.sorted
  def descending(dates: DateTime*): Seq[DateTime] = ascending(dates:_*).reverse
  def max(dates: DateTime*): DateTime = descending(dates:_*).head
  def min(dates: DateTime*): DateTime = ascending(dates:_*).head

  object TimePlace extends Enumeration {
    case class V(name: String) extends Val(name)
    val PAST = V("past")
    val CURRENT = V("current")
    val FUTURE = V("future")
  }

  lazy val MILLISECONDS_IN_SECOND = 1000

  lazy val SECONDS_IN_MINUTE = 60
  lazy val MILLISECONDS_IN_MINUTE = MILLISECONDS_IN_SECOND * SECONDS_IN_MINUTE

  lazy val MINUTES_IN_HOUR = 60
  lazy val SECONDS_IN_HOUR = SECONDS_IN_MINUTE * MINUTES_IN_HOUR

  lazy val HOURS_IN_DAY = 24
  lazy val MILLISECONDS_IN_HOUR = MILLISECONDS_IN_MINUTE * MINUTES_IN_HOUR
  lazy val MILLISECONDS_IN_DAY = MILLISECONDS_IN_HOUR * HOURS_IN_DAY

  lazy val DAYS_IN_YEAR_AVERAGE = ((365f * 4) -1) / 4
  lazy val DAYS_IN_WEEK = 7
  lazy val DAYS_IN_MONTH_AVERAGE = (365f * 4) / (12 * 4)

  lazy val MONTHS_IN_YEAR = 12

  def durationSeconds(count: Int) = new Duration(count * MILLISECONDS_IN_SECOND)
  def durationMinute(count: Int) = new Duration(count * MILLISECONDS_IN_MINUTE)
  def durationHour(count: Int) = new Duration(count * MILLISECONDS_IN_HOUR)
  def seconds(duration: Duration): Float = duration.getMillis.toFloat / MILLISECONDS_IN_SECOND

  def minutes(duration: Duration): Float = duration.getMillis.toFloat / MILLISECONDS_IN_MINUTE
  def minutesFromHours(hours: Float): Float = hours * MINUTES_IN_HOUR

  def hours(duration: Duration): Float = duration.getMillis.toFloat / MILLISECONDS_IN_HOUR
  def days(duration: Duration): Float = duration.getMillis.toFloat / MILLISECONDS_IN_DAY
}

abstract class DateInterval[DI <: DateInterval[DI]](val meta: DateIntervalMeta[DI]) {

  this: DI =>

  def start: ReadableInstant
  lazy val startDT = new DateTime(start)
  lazy val startTimestamp = new Timestamp(start.getMillis)
  lazy val startMillis = start.getMillis

  def end: ReadableInstant
  lazy val endDT = new DateTime(end)
  lazy val endTimestamp = new Timestamp(end.getMillis)
  lazy val endMillis = end.getMillis

  def next = meta.apply(endDT)
  def nextStream: Stream[DI] = {
    def s(di: DI): Stream[DI] = Stream.cons(di.next, s(di.next))
    s(this)
  }
  def nextInclusiveStream: Stream[DI] = {
    def s(di: DI): Stream[DI] = Stream.cons(di, s(di.next))
    s(this)
  }
  def next(n: Int) = nextStream.take(n).toList
  def nextInclusive(n: Int) = nextInclusiveStream.take(n).toList

  def prev = meta.apply(startDT.minusMillis(1))
  def prevStream: Stream[DI] = {
    def s(di: DI): Stream[DI] = Stream.cons(di.prev, s(di.prev))
    s(this)
  }
  def prev(n: Int) = prevStream.take(n).toList

  def period = new Period(start, end)
  def interval = new Interval(start, end)

  def isPast = end.isBefore(DateUtil.now)
  def isCurrent = interval.contains(DateUtil.now)
  def isFuture = start.isAfter(DateUtil.now)
  def timePlace: DateUtil.TimePlace.V = (isCurrent, isPast) match {
    case (true, _) => TimePlace.CURRENT
    case (_, true) => TimePlace.PAST
    case (false, false) => TimePlace.FUTURE
  }

  def contains(di: DI) =
    (this.start.isBefore(di.start) || this.start.equals(di.start)) &&
      (this.end.isAfter(di.end) || this.start.equals(di.end))

  override def toString = intervalString
  def intervalString = "<%s - %s)".format(
    DateUtil.formatTime(startDT),
    DateUtil.formatTime(endDT))
}

trait DateIntervalMeta[DI <: DateInterval[DI]] {
  def apply(dt: DateTime): DI
  def current = apply(DateTime.now)
  def surrounding(interval: Interval): Stream[DI] = surrounding(interval.getStart, interval.getEnd)
  def surrounding(after: DateTime, before: DateTime): Stream[DI] = apply(after).nextInclusiveStream.takeWhile(_.start isBefore before)
}

// YEAR

object Year
  extends DateIntervalMeta[Year] {
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

  override def toString =
    "%s %s" format (start.monthOfYear.getAsShortText, start.yearOfEra.getAsText)
}

// MONTH

object Month
  extends DateIntervalMeta[Month] {
  def apply(year: Int, month: Int): Month = Month(DateUtil.date(year, month, 1))
}

case class Month(dt: DateTime)
  extends DateInterval[Month](Month) {
  lazy val start = dt.withDayOfMonth(1).withTimeAtStartOfDay()
  lazy val end = start plusMonths 1

  lazy val monthOfYear = start.getMonthOfYear
  lazy val firstDay = Day(start)
  lazy val firstWeek = Week(start)
  lazy val days: Stream[Day] = firstDay
    .nextInclusiveStream
    .takeWhile(d => d.start.isBefore(end))
  lazy val weeks: Stream[Week] = firstWeek
    .nextInclusiveStream
    .takeWhile(w => w.start.isBefore(end))

  override def toString =
    "%s %s" format (start.monthOfYear.getAsShortText, start.yearOfEra.getAsText)
}

// WEEK

object Week extends DateIntervalMeta[Week]

case class Week(dt: DateTime)
  extends DateInterval[Week](Week) {
  lazy val start = dt.withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay()
  lazy val end = start plusWeeks 1

  lazy val firstDay = Day(start)
  def days = firstDay :: firstDay.next(6)
}

// DAY

object Day extends DateIntervalMeta[Day] {
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
}

case class Day(dt: DateTime)
  extends DateInterval[Day](Day) {
  lazy val start = dt.withTimeAtStartOfDay
  lazy val end = start plusDays 1

  lazy val daytimeInterval = new Interval(start.withHourOfDay(Day.DAYTIME_START_HOUR), start.withHourOfDay(Day.DAYTIME_END_HOUR))

  def dayOfWeek = start.getDayOfWeek // Mon == 1, Tue == 2... Sun == 7
  def dayOfMonth = start.getDayOfMonth
  def month = Month(start)
  def firstHour = Hour(start)
  def hours = firstHour.nextInclusive(24)

  def isHoliday = dayOfWeek >= 6

  override def toString = "%s - %s/%s/%s" format (start.dayOfWeek.getAsText,
    start.getYear, start.getMonthOfYear, start.getDayOfMonth)
}

// HOUR

object Hour extends DateIntervalMeta[Hour]

case class Hour(dt: DateTime)
  extends DateInterval[Hour](Hour) {
  lazy val start = dt.withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0)
  lazy val end = start plusHours 1

  override def toString = DateUtil.formatHour(start)
}

// MINUTE

object Minute extends DateIntervalMeta[Minute]

case class Minute(dt: DateTime)
  extends DateInterval[Minute](Minute) {
  lazy val start = dt.withSecondOfMinute(0).withMillisOfSecond(0)
  lazy val end = start plusMinutes 1

  override def toString = DateUtil.formatHour(start)
}
