package commons.docs

import java.io._

import org.apache.poi.ss.usermodel._
import org.apache.poi.ss.util.{NumberToTextConverter, CellReference}

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

  def save(wb: Workbook, path: String): Unit = {
    val fileOut = new FileOutputStream(path)
    wb.write(fileOut)
    fileOut.close()
  }
}

case class WorkbookHelper(wb: Option[Workbook] = None, file: Option[File] = None, input: Option[InputStream] = None, path: Option[String] = None, dropFirstRows: Int = 0) {
  require(List(wb, file, input, path).flatten.nonEmpty)
  val workbook: Workbook =
    List(wb, file.map(Excel.workbook), input.map(Excel.workbook), path.map(Excel.workbook))
      .flatten.headOption.get
  val sheets: Stream[Sheet] = (0 until workbook.getNumberOfSheets).map(i => workbook.getSheetAt(i)).toStream
  val sheetHelpers: Stream[SheetHelper] = sheets.map(s => SheetHelper.apply(s, dropFirstRows))
  val rows: Stream[Row] = sheetHelpers.flatMap(_.rows)
  val rowHelpers: Stream[RowHelper] = rows.map(RowHelper.apply)
  def rows[R](convert: (RowHelper => R)): Stream[R] = rowHelpers.map(convert)
}

abstract class AbstractSheetHelper {

  def sheet: Sheet

  protected def letterToIndex(letter: String): Int = {
    val c = CellReference.convertColStringToIndex(letter)
    c
  }

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
}

case class SheetHelper(sheet: Sheet, dropFirstRows: Int = 0)
  extends AbstractSheetHelper {

  def valueString(row: Row, col: Int): Option[String] = RowHelper(row).valueString(col)
  def valueString(row: Int, col: Int): Option[String] = RowHelper(sheet.getRow(row)).valueString(col)
  def valueString(row: Row, col: String): Option[String] = RowHelper(row).valueString(col)
  def valueString(row: Int, col: String): Option[String] = valueString(sheet.getRow(row), col)

  def valueBoolean(row: Row, col: Int): Option[Boolean] = RowHelper(row).valueBoolean(col)
  def valueBoolean(row: Int, col: Int): Option[Boolean] = RowHelper(sheet.getRow(row)).valueBoolean(col)
  def valueBoolean(row: Row, col: String): Option[Boolean] = RowHelper(row).valueBoolean(col)
  def valueBoolean(row: Int, col: String): Option[Boolean] = RowHelper(sheet.getRow(row)).valueBoolean(col)

  def valueNumeric(row: Row, col: Int): Option[Double] = RowHelper(row).valueNumeric(col)
  def valueNumeric(row: Int, col: Int): Option[Double] = RowHelper(sheet.getRow(row)).valueNumeric(col)
  def valueNumeric(row: Row, col: String): Option[Double] = RowHelper(row).valueNumeric(col)
  def valueNumeric(row: Int, col: String): Option[Double] = RowHelper(sheet.getRow(row)).valueNumeric(col)

  def set(row: Int, col: Int, values: Any*): Seq[Cell] = RowHelper(sheet.getRow(row)).set(col, values)

  def rows: Stream[Row] = sheet.rowIterator.toStream.drop(dropFirstRows)
  def rowHelpers: Stream[RowHelper] = rows.map(RowHelper.apply)
  def rows[R](convert: (RowHelper => R)): Stream[R] = rowHelpers.map(convert)
}

case class RowHelper(row: Row) {

  protected def letterToIndex(letter: String): Int = {
    val c = CellReference.convertColStringToIndex(letter)
    c
  }

  def valueString(col: Int): Option[String] = {
    val c = row.getCell(col)
    val tries = Try(c.getStringCellValue).toOption ::
      Try(NumberToTextConverter.toText(c.getNumericCellValue)).toOption :: Nil
    tries.flatten.headOption.filter(_.nonEmpty)
  }
  def valueString(col: String): Option[String] = valueString(letterToIndex(col))

  def valueBoolean(col: Int): Option[Boolean] = Try(row.getCell(col).getBooleanCellValue).toOption
  def valueBoolean(col: String): Option[Boolean] = valueBoolean(letterToIndex(col))

  def valueNumeric(col: Int): Option[Double] = Try(row.getCell(col).getNumericCellValue).toOption
  def valueNumeric(col: String): Option[Double] = valueNumeric(letterToIndex(col))

  def cell(col: Int): Cell = {
    row.getCell(col) match {
      case null => row.createCell(col)
      case cell => cell
    }
  }
  def cell(col: String): Cell = cell(letterToIndex(col))

  def set(col: Int, values: Any*): Seq[Cell] = {
    values.zipWithIndex map {
      case (value, index) =>
        val c = cell(col + index)
        value match {
          case s: String => c.setCellValue(s)
          case i: Int => c.setCellValue(i)
          case f: Float => c.setCellValue(f)
          case d: Double => c.setCellValue(d)
          case x => c.setCellValue(x.toString)
        }
        c
    }
  }
}

case class SheetRegionHelper(sheet: Sheet, rowOffset: Int, colOffset: Int, maxRow: Option[Int] = None, maxCol: Option[Int] = None)
  extends AbstractSheetHelper {

  val sh = SheetHelper(sheet)

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
  def valueString(row: Int, col: String): Option[String] = {
    val r = relativeRow(row)
    sh.valueString(r, col)
  }

  def valueBoolean(row: Int, col: Int): Option[Boolean] = {
    val r = relativeRow(row)
    val c = relativeCol(col)
    sh.valueBoolean(r, c)
  }
  def valueBoolean(row: Int, col: String): Option[Boolean] = {
    val r = relativeRow(row)
    sh.valueBoolean(r, col)
  }

  def valueNumeric(row: Int, col: Int): Option[Double] = {
    val r = relativeRow(row)
    val c = relativeCol(col)
    sh.valueNumeric(r, c)
  }
  def valueNumeric(row: Int, col: String): Option[Double] = {
    val r = relativeRow(row)
    sh.valueNumeric(r, col)
  }

  override def toString = s"Sheet region, row offset: $rowOffset, column offset: $colOffset"
}
