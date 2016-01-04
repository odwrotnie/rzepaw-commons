package commons.docs

import java.io._

import org.apache.poi.ss.usermodel._
import org.apache.poi.ss.util.{NumberToTextConverter, CellReference}

import scala.collection.JavaConversions._
import scala.util.Try

object Excel {

  def workbook(file: File): Workbook = WorkbookFactory.create(file)
  def workbook(bytes: Array[Byte]): Workbook = workbook(new ByteArrayInputStream(bytes))
  def workbook(is: InputStream): Workbook = WorkbookFactory.create(is)
  def workbook(path: String): Workbook = try {
    workbook(new FileInputStream(path)) // Needs more memory
  } catch {
    case t: Throwable => workbook(new File(path))
  }

  def save(wb: Workbook, path: String): Unit = {
    val fileOut = new FileOutputStream(path)
    wb.write(fileOut)
    fileOut.close()
  }
}
