package commons.files

import java.io.{File, FileWriter}
import java.net.URL

import com.typesafe.scalalogging.LazyLogging

import scala.io.Codec
import scala.util.Try
import scala.xml.{Elem, XML}

trait FilesFromResourcesSupport
  extends LazyLogging {

  def codec = Codec.UTF8

  def listFromResources(path: String*): List[File] = {
    val pathString = path.mkString("/", "/", "")
    val dirUrl: URL = getClass.getResource(pathString)
    val dir = new File(dirUrl.toURI)
    require(dir.isDirectory)
    val files = dir.listFiles().toList
    logger.debug(s"Files in resources${ pathString }: ${ files.mkString(", ") }")
    files
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
