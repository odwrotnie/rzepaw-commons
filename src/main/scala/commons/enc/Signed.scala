package commons.enc

import commons.date.DateUtil
import commons.logger.Logger
import org.joda.time.DateTime

import scala.xml.Elem

abstract class Signed[D](data: D, pub: String, prv: String)
  extends Logger {

  lazy val signer = Signer.generateRandom

  def dataToBytes: Array[Byte]
  lazy val sig = signer.sign(dataToBytes)
  lazy val sigBytes = sig.sign
  lazy val sigString = Signer.bytesToBase64(sigBytes)
}

case class SignedXML(data: Elem, pub: String, prv: String, date: DateTime = DateUtil.now)
  extends Signed[Elem](data, pub, prv) {
  // TODO Inaczej pobrac bajty xmla
  lazy val dataToBytes = data.toString().getBytes
  lazy val signedData = <signed>
    <date>{ DateUtil.formatTime(date) }</date>
    <data>{ data }</data>
    <signature alg={ Signer.ALGORITHM } rand={ Signer.RANDOMIZER } digest={ Signer.MESSAGE_DIGEST_ALGORITHM } >
      { sigString }
    </signature>
  </signed>
}
