package commons.enc

import commons.logger.Logger
import org.scalatest.FunSuite

// sbt "~commons/testOnly commons.enc.HasherTest"
class HasherTest
  extends FunSuite
  with Logger {

  test("Test compare") {
    val correctPassword = "Wext123?"
    val incorrectPassword = "W"
    val hashed = Hasher.encrypt(correctPassword)

    assert(Hasher.equal(hashed, correctPassword), s"Hashed password: $correctPassword should equal $hashed")
    assert(!Hasher.equal(hashed, incorrectPassword), s"Hashed password: $incorrectPassword should not equal $hashed")
  }
}
