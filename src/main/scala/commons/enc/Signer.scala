package commons.enc

import java.io._
import java.security._
import java.security.spec._
import javax.xml.bind.DatatypeConverter

import commons.logger.Logger

case class Signer(val keyPublic: PublicKey, val keyPrivate: PrivateKey) {

  val keyPrivateEncoded: Array[Byte] = keyPrivate.getEncoded
  val keyPrivateBase64 = Signer.bytesToBase64(keyPrivateEncoded)
  val keyPublicEncoded: Array[Byte] = keyPublic.getEncoded
  val keyPublicBase64 = Signer.bytesToBase64(keyPublicEncoded)
  val dsa: Signature = Signature.getInstance(Signer.MESSAGE_DIGEST_ALGORITHM, Signer.ALGORITHM_PROVIDER)
  dsa.initSign(keyPrivate)

  def sign(bytes: Array[Byte]): Signature = sign(new ByteArrayInputStream(bytes))
  def sign(is: InputStream): Signature = {
    val bis: BufferedInputStream = new BufferedInputStream(is)
    val buffer: Array[Byte] = new Array(Signer.BUFFER_SIZE)
    var len = 0
    while ({ len = bis.read(buffer); len >= 0 }) {
      dsa.update(buffer, 0, len)
    }
    bis.close
    dsa
  }
}

object Signer
  extends Logger {

  val BUFFER_SIZE = 1024
  val ALGORITHM = "DSA"
  val ALGORITHM_PROVIDER = "SUN"
  val RANDOMIZER = "SHA1PRNG"
  val MESSAGE_DIGEST_ALGORITHM = "SHA1withDSA"
  val keyFactory: KeyFactory = KeyFactory.getInstance(ALGORITHM, ALGORITHM_PROVIDER)
  val generator: KeyPairGenerator = KeyPairGenerator.getInstance(Signer.ALGORITHM, Signer.ALGORITHM_PROVIDER)
  generator.initialize(Signer.BUFFER_SIZE, SecureRandom.getInstance(Signer.RANDOMIZER, Signer.ALGORITHM_PROVIDER))

  // TODO http://j2stuff.blogspot.com/2012/03/generate-public-and-private-keys-to.html
  def apply(pub: String, prv: String): Signer = {
    val pubKey = base64ToBytes(pub)
    val prvKey = base64ToBytes(prv)
    apply(pubKey, prvKey)
  }
  def apply(pub: Array[Byte], prv: Array[Byte]): Signer = {
    val pubKey: PublicKey = keyFactory.generatePublic(new X509EncodedKeySpec(pub))
    val prvKey: PrivateKey = keyFactory.generatePrivate(new X509EncodedKeySpec(prv))
    Signer(pubKey, prvKey)
  }
  def generateRandom = {
    val keys = generator.generateKeyPair
    Signer(keys.getPublic, keys.getPrivate)
  }

  def verify(keyEncoded: Array[Byte], signature: Array[Byte], is: InputStream) = {
    val pubKey: PublicKey = keyFactory.generatePublic(new X509EncodedKeySpec(keyEncoded))
    val sig: Signature = Signature.getInstance(MESSAGE_DIGEST_ALGORITHM, ALGORITHM_PROVIDER);
    sig.initVerify(pubKey)
    val bis: BufferedInputStream = new BufferedInputStream(is)
    val buffer: Array[Byte] = new Array(BUFFER_SIZE)
    var len = 0
    while (bis.available != 0) {
      len = bis.read(buffer)
      sig.update(buffer, 0, len)
    }
    bis.close
    sig.verify(signature)
  }

  def base64ToBytes(s: String): Array[Byte] = DatatypeConverter.parseBase64Binary(s)
  def bytesToBase64(b: Array[Byte]): String = DatatypeConverter.printBase64Binary(b)
}
