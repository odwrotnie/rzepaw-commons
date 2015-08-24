package commons.date.test

import commons.date.DateUtil
import commons.logger.Logger
import org.ocpsoft.prettytime.PrettyTime
import org.scalatest.FunSuite

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
}
