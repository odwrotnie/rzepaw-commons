package commons.logger

import org.scalatest.FunSuite

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/*
sbt "~rzepawCommons/testOnly commons.logger.GoogleMessagingTest"
 */
class GoogleMessagingTest
  extends FunSuite
  with Logger {

  test("Send notification") {
    val gm = GoogleMessaging("Scalatest", "???")
    val response = gm.notify("The message - żółć!", "/")
    info("Response: " + Await.result(response, Duration.Inf))
  }
}
