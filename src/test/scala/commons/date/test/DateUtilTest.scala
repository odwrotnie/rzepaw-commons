package commons.date.test

import commons.date.DateUtil
import commons.logger.Logger
import org.joda.time.Duration
import org.ocpsoft.prettytime.PrettyTime
import org.scalatest.FunSuite

/*
sbt "~rzepawCommons/testOnly commons.date.test.DateUtilTest"
 */
class DateUtilTest
  extends FunSuite
  with Logger {

  val pt = new PrettyTime

  test("Date util test") {

    (1 to 30) foreach { i =>
      val minutes = i * 7
      val date = DateUtil.now.plusHours(1).minusMinutes(minutes)
      val ago = DateUtil.humanReadable(date)
      info(s"$date - $ago")
    }
  }

  test("Duration test") {
    val s = DateUtil.now
    val e = s.plusHours(3)
    val d = new Duration(s, e)
    println("SECONDS: " + d.getStandardSeconds)
    println("MINUTES: " + d.getStandardMinutes)
    println("HOURS: " + d.getStandardHours)
    assert(d.getMillis == 10800000)
  }
}
