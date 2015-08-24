package commons.files

import commons.logger.Logger
import org.scalatest._

// sbt "~commons/testOnly commons.files.FilesTest"
class AddressTest
  extends FunSuite
  with BeforeAndAfter
  with Logger {

  test("New file") {
    val f = TextFile("/tmp/test-file.txt")
    f.append("?")
  }
}
