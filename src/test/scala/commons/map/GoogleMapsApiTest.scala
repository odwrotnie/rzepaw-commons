package commons.map

import commons.logger.Logger
import org.scalatest.FunSuite

/*
sbt "~rzepawCommons/testOnly commons.map.GoogleMapsApiTest"
 */

class GoogleMapsApiTest
  extends FunSuite
  with Logger {

  val a = "Bydgoszcz"
  val b = "Berlin"

  test("Distance test") {
    println("Distance: " + Distance.between(a, b))
    println("Location: " + Geolocation.calculate(a))
    println("Location: " + Geolocation.calculate(b))
  }
}
