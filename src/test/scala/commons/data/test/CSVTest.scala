package commons.data.test

import commons.data.Table
import commons.logger.Logger
import org.scalatest.FunSuite

class CSVTest
  extends FunSuite
  with Logger {

  test("CSV") {

    val table = Table(List("left", "right"), List(1, 2), List(3, 4))
    info(table.print())
  }
}
