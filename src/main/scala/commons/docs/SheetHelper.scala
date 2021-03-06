package commons.docs

import org.apache.poi.ss.util.CellReference
import org.joda.time.DateTime

import scala.collection.JavaConversions._
import org.apache.poi.ss.usermodel.{Cell, Row, Sheet}

import scala.util.Try

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

  def name = sheet.getSheetName

  def valueString(row: Row, col: Int): Option[String] = RowHelper(row, this).valueString(col)
  def valueString(row: Int, col: Int): Option[String] = RowHelper(sheet.getRow(row), this).valueString(col)
  def valueString(row: Row, col: String): Option[String] = RowHelper(row, this).valueString(col)
  def valueString(row: Int, col: String): Option[String] = valueString(sheet.getRow(row), col)

  def valueBoolean(row: Row, col: Int): Option[Boolean] = RowHelper(row, this).valueBoolean(col)
  def valueBoolean(row: Int, col: Int): Option[Boolean] = RowHelper(sheet.getRow(row), this).valueBoolean(col)
  def valueBoolean(row: Row, col: String): Option[Boolean] = RowHelper(row, this).valueBoolean(col)
  def valueBoolean(row: Int, col: String): Option[Boolean] = RowHelper(sheet.getRow(row), this).valueBoolean(col)

  def valueNumeric(row: Row, col: Int): Option[Double] = RowHelper(row, this).valueNumeric(col)
  def valueNumeric(row: Int, col: Int): Option[Double] = RowHelper(sheet.getRow(row), this).valueNumeric(col)
  def valueNumeric(row: Row, col: String): Option[Double] = RowHelper(row, this).valueNumeric(col)
  def valueNumeric(row: Int, col: String): Option[Double] = RowHelper(sheet.getRow(row), this).valueNumeric(col)

  def valueDate(row: Row, col: Int): Option[DateTime] = RowHelper(row, this).valueDate(col)
  def valueDate(row: Int, col: Int): Option[DateTime] = RowHelper(sheet.getRow(row), this).valueDate(col)
  def valueDate(row: Row, col: String): Option[DateTime] = RowHelper(row, this).valueDate(col)
  def valueDate(row: Int, col: String): Option[DateTime] = valueDate(sheet.getRow(row), col)

  def set(rowIndex: Int, col: Int, values: Any*): Seq[Cell] = {
    val row: Row = Option(sheet.getRow(rowIndex)).getOrElse(sheet.createRow(rowIndex))
    RowHelper(row, this).set(col, values:_*)
  }

  def rows: Stream[Row] = sheet.rowIterator.toStream.drop(dropFirstRows)
  def rowHelpers: Stream[RowHelper] = rows.map(row => RowHelper(row, this))
  def rows[R](convert: (RowHelper => R)): Stream[R] = rowHelpers.map(convert)

  override def toString: String = s"Sheet $name"
}
