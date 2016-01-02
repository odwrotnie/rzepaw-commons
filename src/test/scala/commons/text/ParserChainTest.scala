package commons.text

import commons.logger.Logger
import org.scalatest.FunSuite

/*
sbt "~rzepawCommons/testOnly commons.text.ParserChainTest"
 */

class ParserChainTest
  extends FunSuite
  with Logger {

  test("Simple") {

    val i: Option[Int] = ParserChain2[Int]("31") +
      (s => 1/0) +
      (s => s.toInt) +
      (s => 1/0) +
      (s => 10) parse

    assert(i.nonEmpty)
    assert(i.contains(31))
  }
}
