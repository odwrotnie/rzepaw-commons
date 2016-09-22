package commons.docs

import org.apache.poi.ss.usermodel.{Cell, Row}
import org.apache.poi.ss.util.{NumberToTextConverter, CellReference}
import org.joda.time.DateTime

import scala.collection.JavaConversions._
import scala.util.Try

case class RowHelper(row: Row, sh: SheetHelper) {

  protected def letterToIndex(letter: String): Option[Int] =
    Try(CellReference.convertColStringToIndex(letter)).toOption
  protected def labelToIndexByHeaderRow(label: String, row: Row): Option[Int] =
    row.cellIterator().find(_.getStringCellValue == label).map(_.getColumnIndex)
  protected def labelToIndexByHeaderRow(label: String, rowIndex: Int): Option[Int] =
    labelToIndexByHeaderRow(label, row.getSheet.getRow(rowIndex))

  def valueString(col: Int): Option[String] = {
    Option(row.getCell(col)) flatMap { c =>
      c.getCellType match {
        case Cell.CELL_TYPE_STRING | Cell.CELL_TYPE_FORMULA => Try(c.getStringCellValue).toOption
        case Cell.CELL_TYPE_NUMERIC => Try(NumberToTextConverter.toText(c.getNumericCellValue)).toOption
        case Cell.CELL_TYPE_BLANK => None
        case _ => None
      }
    }
  }
  def valueString(col: String): Option[String] = letterToIndex(col).flatMap(valueString)
  def valueStringByLabel(col: String, labelsIndex: Int = 0): Option[String] = labelToIndexByHeaderRow(col, labelsIndex).flatMap(valueString)

  def valueBoolean(col: Int): Option[Boolean] = Try(row.getCell(col).getBooleanCellValue).toOption
  def valueBoolean(col: String): Option[Boolean] = letterToIndex(col).flatMap(valueBoolean)
  def valueBooleanByLabel(col: String, labelsIndex: Int = 0): Option[Boolean] = labelToIndexByHeaderRow(col, labelsIndex).flatMap(valueBoolean)

  def valueNumeric(col: Int): Option[Double] = Try(row.getCell(col).getNumericCellValue).toOption
  def valueNumeric(col: String): Option[Double] = letterToIndex(col).flatMap(valueNumeric)
  def valueNumericByLabel(col: String, labelsIndex: Int = 0): Option[Double] = labelToIndexByHeaderRow(col, labelsIndex).flatMap(valueNumeric)

  def valueDate(col: Int): Option[DateTime] = Try(new DateTime(row.getCell(col).getDateCellValue)).toOption
  def valueDate(col: String): Option[DateTime] = letterToIndex(col).flatMap(valueDate)
  def valueDateByLabel(col: String, labelsIndex: Int = 0): Option[DateTime] = labelToIndexByHeaderRow(col, labelsIndex).flatMap(valueDate)

  def cell(col: Int): Option[Cell] = Option(row.getCell(col))
  def cell(col: String): Option[Cell] = letterToIndex(col).flatMap(cell)

  def set(col: Int, values: Any*): Seq[Cell] = {
    values.zipWithIndex map {
      case (value, index) =>
        val c: Cell = cell(col + index).getOrElse(row.createCell(col))
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

  // def set(col: String, values: Any*) = letterToIndex(col).map(i => set(i, values))
}
