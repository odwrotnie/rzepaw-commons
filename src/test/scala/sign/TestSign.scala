package sign

import commons.logger.Logger
import commons.enc.SignedXML
import org.scalatest.FunSuite

class TestSign
  extends FunSuite
  with Logger {

  test("Test sign") {
    val xml = <h1>Jakiś dokument zajebisty</h1>
    val sXml = SignedXML(xml, "?", "?")
    info(sXml.signedData)
  }
}
