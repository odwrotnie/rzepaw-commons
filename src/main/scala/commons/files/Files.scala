package commons.files

import java.io.{File, FileWriter}
import java.net.URL

import scala.util.Try

object Files {

  def listFromResources(directoryInResources: String): List[File] = {
    val dirUrl: URL = getClass.getResource(directoryInResources)
    val dir = new File(dirUrl.toURI)
    require(dir.isDirectory)
    dir.listFiles().toList
  }

  def textFromResources(path: String*): Option[String] = Try {
    val is = getClass.getResourceAsStream(path.mkString("/", "/", ""))
    scala.io.Source.fromInputStream(is).mkString
  }.toOption
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
