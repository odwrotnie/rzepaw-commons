package commons.docs

import java.io.{InputStream, File}

import org.apache.poi.ss.usermodel.{Row, Sheet, Workbook}

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
