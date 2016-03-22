package commons.text

import org.scalatest.FlatSpec

/*
sbt "~rzepawCommons/testOnly commons.text.PatternTest"
 */

class PatternTest
  extends FlatSpec {

  "Pattern" should "be found in the text" in {
    val s = "żółć"
    val text = s"asdf${s}qwer"
    assert(Pattern.pickFirstString(s, text).isDefined)
  }

  "Pattern" should "not be found in the text" in {
    val s = "żółć"
    val text = s"asdfqwer"
    assert(Pattern.pickFirstString(s, text).isEmpty)
  }
}
