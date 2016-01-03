package commons.docs

import org.apache.poi.ss.util.CellReference

import scala.collection.JavaConversions._
import org.apache.poi.ss.usermodel.{Cell, Row, Sheet}

abstract class AbstractSheetHelper {

  def sheet: Sheet

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
