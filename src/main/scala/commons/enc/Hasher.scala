package commons.enc

// import com.roundeights.hasher.Implicits._
import scala.language.postfixOps

object Hasher {

  def encrypt(word: String): Array[Byte] = {
    // word.md5.bytes
    Array[Byte]()
  }

  def equal(hashed: Array[Byte], word: String): Boolean = {
    // word.md5 hash= hashed
    true
  }
}
