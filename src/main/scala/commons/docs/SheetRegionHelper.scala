package commons.docs

import org.apache.poi.ss.usermodel.Sheet

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
