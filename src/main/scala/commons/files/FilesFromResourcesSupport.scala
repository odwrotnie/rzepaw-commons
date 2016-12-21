package commons.files

import java.io.{File, FileWriter}
import java.net.URL

import scala.io.Codec
import scala.util.Try
import scala.xml.{Elem, XML}

trait FilesFromResourcesSupport {

  def codec = Codec.UTF8

  def listFromResources(directoryInResources: String): List[File] = {
    val dirUrl: URL = getClass.getResource(directoryInResources)
    val dir = new File(dirUrl.toURI)
    require(dir.isDirectory)
    dir.listFiles().toList
  }

  def textFromResources(path: String*): String = {
    val pathString = path.mkString("/", "/", "")
    val is = getClass.getResourceAsStream(pathString)
    scala.io.Source.fromInputStream(is)(codec).getLines().mkString("\n")
  }

  def xmlFromResources(path: String*): Elem = {
    val pathString = path.mkString("/", "/", "")
    val uri = getClass.getResource(pathString).toURI
    val file = new File(uri)
    XML.loadFile(file)
  }
}
