package commons.docs

import org.apache.poi.ss.usermodel.{Cell, Row}
import org.apache.poi.ss.util.{NumberToTextConverter, CellReference}

import scala.collection.JavaConversions._
import scala.util.Try

case class RowHelper(row: Row) {

  protected def letterToIndex(letter: String): Option[Int] = Try(CellReference.convertColStringToIndex(letter)).toOption
  protected def labelToIndexByHeaderRow(label: String, row: Row): Option[Int] = row.cellIterator().find(_.getStringCellValue == label).map(_.getColumnIndex)
  protected def labelToIndexByHeaderRow(label: String, rowIndex: Int): Option[Int] = labelToIndexByHeaderRow(label, row.getSheet.getRow(rowIndex))

  def valueString(col: Int): Option[String] = {
    val c = row.getCell(col)
    val tries = Try(c.getStringCellValue).toOption ::
      Try(NumberToTextConverter.toText(c.getNumericCellValue)).toOption :: Nil
    tries.flatten.headOption.filter(_.nonEmpty)
  }
  def valueString(col: String): Option[String] = letterToIndex(col).flatMap(valueString)
  def valueStringByLabel(col: String, labelsIndex: Int = 0): Option[String] = labelToIndexByHeaderRow(col, labelsIndex).flatMap(valueString)

  def valueBoolean(col: Int): Option[Boolean] = Try(row.getCell(col).getBooleanCellValue).toOption
  def valueBoolean(col: String): Option[Boolean] = letterToIndex(col).flatMap(valueBoolean)
  def valueBooleanByLabel(col: String, labelsIndex: Int = 0): Option[Boolean] = labelToIndexByHeaderRow(col, labelsIndex).flatMap(valueBoolean)

  def valueNumeric(col: Int): Option[Double] = Try(row.getCell(col).getNumericCellValue).toOption
  def valueNumeric(col: String): Option[Double] = letterToIndex(col).flatMap(valueNumeric)
  def valueNumericByLabel(col: String, labelsIndex: Int = 0): Option[Double] = labelToIndexByHeaderRow(col, labelsIndex).flatMap(valueNumeric)

  def cell(col: Int): Cell = {
    row.getCell(col) match {
      case null => row.createCell(col)
      case cell => cell
    }
  }
  def cell(col: String): Option[Cell] = letterToIndex(col).map(cell)

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
