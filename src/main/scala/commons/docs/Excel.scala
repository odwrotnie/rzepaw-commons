package commons.docs

import java.io._

import org.apache.poi.ss.usermodel._
import org.apache.poi.ss.util.CellReference

import scala.collection.JavaConversions._
import scala.util.Try

object Excel {

  def workbook(file: File): Workbook = WorkbookFactory.create(file)
  def workbook(is: InputStream): Workbook = WorkbookFactory.create(is)
  def workbook(path: String): Workbook = try {
    workbook(new FileInputStream(path)) // Needs more memory
  } catch {
    case t: Throwable => workbook(new File(path))
  }
}

case class WorkbookManipulator(val workbook: Workbook) {

  def cellStringValue(letter: String)(implicit r: Row): Option[String] =
    cell(letter).flatMap { c =>
      List(Try(c.getStringCellValue).toOption,
        Try(c.getNumericCellValue.toString).toOption).flatten.headOption
    }
  def cellBooleanValue(letter: String)(implicit r: Row): Option[Boolean] =
    cell(letter).map(_.getBooleanCellValue)
  def cellDoubleValue(letter: String)(implicit r: Row): Option[Double] =
    cell(letter).flatMap { c =>
      List(Try(c.getNumericCellValue).toOption,
        Try(c.getStringCellValue.toDouble).toOption).flatten.headOption
    }

  private def letterToIndex(letter: String) =
    CellReference.convertColStringToIndex(letter)
  def cell(letter: String)(implicit r: Row): Option[Cell] =
    r.getCell(letterToIndex(letter)) match {
      case null => None
      case c: Cell if c.getCellType == Cell.CELL_TYPE_BLANK => None
      case c: Cell => Some(c)
    }
  def cellGetOrCreate(letter: String)(implicit r: Row): Cell = {
    cell(letter) match {
      case Some(c) => c
      case _ => r.createCell(letterToIndex(letter))
    }
  }

  def save(os: OutputStream) = workbook.write(os)
}

abstract class SheetConverter[T](val workbook: Workbook) {

  protected val wm = WorkbookManipulator(workbook)

  def fromRow(implicit r: Row): T

  lazy val rows: List[Row] = (for {
    sheet <- List(workbook.getSheetAt(0))
    row <- sheet
  } yield row)
  lazy val list: Stream[T] = {
    rows.toStream.map(r => Try(fromRow(r)).toOption).flatten
  }
}
