package commons.logger

import org.scalatest.FunSuite

/*
sbt "~rzepawCommons/testOnly commons.logger.GoogleMessagingTest"
 */
class GoogleMessagingTest
  extends FunSuite
  with Logger {

  test("Send notification") {
    val gm = GoogleMessaging("Scalatest", "???")
    gm.notify("The message - żółć!")
  }
}
