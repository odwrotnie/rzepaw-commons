package commons.date

import java.sql.Timestamp

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
