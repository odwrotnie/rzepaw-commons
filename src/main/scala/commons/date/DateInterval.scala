package commons.date

import java.sql.Timestamp
import org.joda.time._

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

  def millis = endMillis - startMillis

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
  def prevInclusiveStream: Stream[DI] = {
    def s(di: DI): Stream[DI] = Stream.cons(di, s(di.prev))
    s(this)
  }
  def prev(n: Int) = prevStream.take(n).toList
  def prevInclusive(n: Int) = prevInclusiveStream.take(n).toList

  def period = new Period(start, end)
  def interval = new Interval(start, end)

  def isPast = end.isBefore(DateUtil.now)
  def isCurrent = interval.contains(DateUtil.now)
  def isFuture = start.isAfter(DateUtil.now)
  def timePlace: TimePlace.V = (isCurrent, isPast) match {
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
  def apply(millis: Long): DI = apply(DateUtil.getDate(millis))
  def current = apply(DateTime.now)
  def surrounding(interval: Interval): Stream[DI] = surrounding(interval.getStart, interval.getEnd)
  def surrounding(after: DateTime, before: DateTime): Stream[DI] = apply(after).nextInclusiveStream.takeWhile(_.start isBefore before)
}
