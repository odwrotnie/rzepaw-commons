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

  "Pattern" should "find all emails in the text" in {
    val text =
      """
        |Lorem ipsum dolor sit@amet.com, consectetur adipiscing elit. Fusce egestas, ex vitae maximus varius, augue
        |lorem luctus erat, ut-convallis@ante.orci facilisis enim. Praesent aliquet orci eu mi fringilla sollicitudin.
        |Cras fermentum mattis lectus, vitae+pretium@tellus.tempor.eu. Vestibulum ac faucibus erat. Vivamus eu faucibus
        |ipsum. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.
      """.stripMargin
    val emails = Pattern.pickAllEmails(text)
    println(emails)
    assert(emails.size == 3)
  }
}
