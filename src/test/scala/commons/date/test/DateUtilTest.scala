package commons.date.test

import commons.date.{Hour, Day, DateUtil}
import commons.logger.Logger
import org.joda.time.{Interval, Duration}
import org.joda.time.format.DateTimeFormat
import org.ocpsoft.prettytime.PrettyTime
import org.scalatest.{FlatSpec, FunSuite}

/*
sbt "~rzepawCommons/testOnly commons.date.test.DateUtilTest"
 */

class DateUtilTest
  extends FlatSpec
  with Logger {

  val pt = new PrettyTime

  "Date util test" should "" in {
    (1 to 30) foreach { i =>
      val minutes = i * 7
      val date = DateUtil.now.plusHours(1).minusMinutes(minutes)
      val ago = DateUtil.humanReadable(date)
      info(s"$date - $ago")
    }
  }

  "Duration" should "have 10800000 millis" in {
    val s = DateUtil.now
    val e = s.plusHours(3)
    val d = new Duration(s, e)
    println("SECONDS: " + d.getStandardSeconds)
    println("MINUTES: " + d.getStandardMinutes)
    println("HOURS: " + d.getStandardHours)
    assert(d.getMillis == 10800000)
  }

  it should "now have 10800000 millis" in {
    val s1 = DateUtil.now
    val e1 = s1.plusHours(1)
    val d1 = new Duration(s1, e1)
    val s2 = s1.plusHours(100)
    val e2 = s2.plusHours(2)
    val d2 = new Duration(s2, e2)
    val d = d1.plus(d2)
    assert(d.getMillis == 10800000)
  }

//  test("Holidays") {
//    Day.current.next(10) foreach { day =>
//      println(s"Day: $day num: ${ day.dayOfWeek } - ${ day.isHoliday }")
//    }
//  }

//  test("Parse date") {
//
//    println("FORMATED: " + DateTimeFormat.forPattern("YYYY/MM/dd").print(DateUtil.date(2016, 1, 3)))
//
//    assert(DateUtil.parseDate("1999/01/01").contains(DateUtil.date(1999, 1, 1)))
//    assert(DateUtil.parseDate("1999.01.01").contains(DateUtil.date(1999, 1, 1)))
//    assert(DateUtil.parseDate("1999.01/01").contains(DateUtil.date(1999, 1, 1)))
//    assert(DateUtil.parseDate("1999/01-01").contains(DateUtil.date(1999, 1, 1)))
//  }

  "Days surrounding 1 hour" should "have 1 element" in {
    val i = new Interval(DateUtil.date(2016, 1, 6).withHourOfDay(10), DateUtil.date(2016, 1, 6).withHourOfDay(11))
    assert(Day.surrounding(i).force.size == 1)
  }

  "Days surrounding 24 hours" should "have 2 elements" in {
    val i = new Interval(DateUtil.date(2016, 1, 6).withHourOfDay(10), DateUtil.date(2016, 1, 7).withHourOfDay(10))
    assert(Day.surrounding(i).force.size == 2)
  }

  "Hour 12:30 to float" should "be 12.5" in {
    assert(Hour.toFloat(DateUtil.now.withTimeAtStartOfDay().plusHours(12).plusMinutes(30)) == 12.5)
  }
}
