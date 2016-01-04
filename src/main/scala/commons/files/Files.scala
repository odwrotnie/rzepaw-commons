package commons.files

import java.io.{File, FileWriter}
import java.net.URL

object Files {
  def listFromResources(directoryInResources: String): List[File] = {
    val dirUrl: URL = getClass.getResource(directoryInResources)
    val dir = new File(dirUrl.toURI)
    require(dir.isDirectory)
    dir.listFiles().toList
  }
}

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
