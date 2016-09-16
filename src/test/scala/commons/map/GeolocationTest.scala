package commons.map

import commons.logger.Logger
import org.scalatest.FunSuite
import slicky.Slicky._

/*
sbt "~rzepawCommons/testOnly commons.map.GeolocationTest"
 */

class GeolocationTest
  extends FunSuite
  with Logger {

  val a = "Bydgoszcz"
  val b = "Berlin"

  test("Distance test") {
    println("Distance: " + Distance.between(a, b))
    println("Location: " + Geolocation.location(a).awaitSafe)
    println("Location: " + Geolocation.location(b).awaitSafe)
  }
}
