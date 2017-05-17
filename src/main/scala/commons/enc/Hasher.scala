package commons.enc

import java.util

import scala.language.postfixOps

object Hasher {

  def bytesDigest(bytes: Array[Byte]): String =
    new java.math.BigInteger(1, bytes).toString(16)

  def encryptToString(word: String): String =
    bytesDigest(encrypt(word))

  def encrypt(word: String): Array[Byte] = {
    val m = java.security.MessageDigest.getInstance("MD5")
    val b = word.getBytes("UTF-8")
    m.update(b, 0, b.length)
    m.digest()
  }

  def equal(hashed: Array[Byte], word: String): Boolean =
    util.Arrays.equals(encrypt(word), hashed)
}
