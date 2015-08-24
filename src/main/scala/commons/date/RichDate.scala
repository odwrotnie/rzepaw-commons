package commons.date

import java.util._

@deprecated("Use JodaDate instead")
class RichDate(val cal: Calendar) {

  cal.clear(Calendar.HOUR)
  cal.clear(Calendar.MINUTE)
  cal.clear(Calendar.SECOND)
  cal.clear(Calendar.MILLISECOND)
  import commons.date.RichDate.Conjunction

  private var last = 0

  def plus(num: Int) = { last = num; this }
  def minus(num: Int) = { last = -num; this }
  def +(num: Int) = plus(num)
  def -(num: Int) = minus(num)

  def months = { cal.add(Calendar.MONTH, last); this }
  def months(and: Conjunction): RichDate = months
  def month = months
  def month(and: Conjunction): RichDate = months

  def years = { cal.add(Calendar.YEAR, last); this }
  def years(and: Conjunction): RichDate = years
  def year = years
  def year(and: Conjunction): RichDate = years

  def days = { cal.add(Calendar.DAY_OF_MONTH, last); this }
  def days(and: Conjunction): RichDate = days
  def day = days
  def day(and: Conjunction): RichDate = days

  def getYear = cal.get(Calendar.YEAR)
  def getMonth = cal.get(Calendar.MONTH)
  def getDay = cal.get(Calendar.DAY_OF_MONTH)
  def getDate = cal.getTime

  override def toString = "%1$TY/%1$Tm/%1$Td" format cal

  def setYear(y: Int) = {
    cal.set(Calendar.YEAR, y)
    this
  }
  def setMonth(m: Int) = {
    cal.set(Calendar.MONTH, m)
    this
  }
  def setDay(d: Int) = {
    cal.set(Calendar.DAY_OF_MONTH, d)
    this
  }

  def daysBetween(d: RichDate) = {
    val diff = d.cal.getTimeInMillis - cal.getTimeInMillis
    diff.asInstanceOf[Double] / 86400000
  }

  def date = cal.getTime
}

object RichDate {

  implicit def date2rich(date: Date): RichDate = RichDate(date)
  implicit def rich2date(rich: RichDate): Date = rich.date

  class Conjunction
  val and = new Conjunction

  def Today = new RichDate(Calendar.getInstance)
  def Tomorrow = Today + 1 day
  def Yesterday = Today - 1 day
  def today = Today
  def tomorrow = Tomorrow
  def yesterday = Yesterday

  def apply(date: java.util.Date) = {
    val c = Calendar.getInstance
    date match {
      case null => Today
      case _ => {
        c.setTime(date)
        new RichDate(c)
      }
    }
  }
}
