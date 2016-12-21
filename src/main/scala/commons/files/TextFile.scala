package commons.files

import java.io.FileWriter

case class TextFile(path: String) {

  val fw = new FileWriter(path, true)

  def append(string: String) {
    fw.append(string)
  }

  def appendln(line: String) {
    append(line)
    append("\n")
  }

  def close {
    fw.flush()
    fw.close()
  }
}
