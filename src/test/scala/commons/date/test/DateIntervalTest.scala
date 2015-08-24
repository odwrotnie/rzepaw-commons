package commons.date.test

import commons.date.{Day, Month, Week}
import commons.logger.Logger
import org.scalatest.FunSuite

// sbt "commons/testOnly commons.date.test.DateIntervalTest"
class DateIntervalTest
  extends FunSuite
  with Logger {

  test("Date interval test") {

    val currentDay = Day.current
    println("Curr: " + currentDay.intervalString)
    println("Curr: " + currentDay.timePlace)
    currentDay.hours foreach { hour =>
      println(hour)
    }

    val prevDay = currentDay.prev
    println("Prev: " + prevDay.intervalString)
    println("Prev: " + prevDay.timePlace)

    val nextDay = currentDay.next
    println("Next: "+ nextDay.intervalString)
    println("Next: "+ nextDay.timePlace)

    currentDay.next(10) foreach { d =>
      println(d)
    }

    val currentWeek = Week.current
    println(currentWeek.days)

    val currentMonth = Month.current
    println(currentMonth.toString)
//    println(currentMonth.weeks)
  }
}
