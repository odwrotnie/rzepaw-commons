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

case class SheetHelper(workbook: Workbook, sheet: Sheet) {

  def row(index: Int): Row = {
    sheet.getRow(index) match {
      case null => sheet.createRow(index)
      case row => row
    }
  }

  def cell(rowIndex: Int, index: Int): Cell = {
    val row = this.row(rowIndex)
    row.getCell(index) match {
      case null => row.createCell(index)
      case cell => cell
    }
  }

  def set(row: Int, col: Int, value: Any): Cell = {
    val c = cell(row, col)
    value match {
      case s: String => c.setCellValue(s)
      case i: Int => c.setCellValue(i)
      case f: Float => c.setCellValue(f)
      case d: Double => c.setCellValue(d)
      case x => c.setCellValue(x.toString)
    }
    c
  }

  def valueString(row: Int, col: Int): Option[String] = {
    Try(sheet.getRow(row).getCell(col).getStringCellValue).toOption
  }
  def valueBoolean(row: Int, col: Int): Option[Boolean] = {
    Try(sheet.getRow(row).getCell(col).getBooleanCellValue).toOption
  }
  def valueNumeric(row: Int, col: Int): Option[Double] = {
    Try(sheet.getRow(row).getCell(col).getNumericCellValue).toOption
  }
}

case class SheetRegionHelper(sh: SheetHelper, rowOffset: Int, colOffset: Int, maxRow: Option[Int] = None, maxCol: Option[Int]) {

  private def relativeRow(row: Int) = {
    val r = row + rowOffset
    maxRow foreach { mr => require(r <= mr) }
    r
  }
  private def relativeCol(col: Int) = {
    val c = col + colOffset
    maxRow foreach { mc => require(c <= mc) }
    c
  }

  def valueString(row: Int, col: Int): Option[String] = {
    val r = relativeRow(row)
    val c = relativeCol(col)
    sh.valueString(r, c)
  }
  def valueBoolean(row: Int, col: Int): Option[Boolean] = {
    val r = relativeRow(row)
    val c = relativeCol(col)
    sh.valueBoolean(r, c)
  }
  def valueNumeric(row: Int, col: Int): Option[Double] = {
    val r = relativeRow(row)
    val c = relativeCol(col)
    sh.valueNumeric(r, c)
  }
}

case class WorkbookManipulator(workbook: Workbook) {

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
