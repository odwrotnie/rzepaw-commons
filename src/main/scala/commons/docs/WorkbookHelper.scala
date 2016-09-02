package commons.docs

import java.io.{InputStream, File}

import commons.logger.Logger
import org.apache.poi.ss.usermodel.{Row, Sheet, Workbook}

import scala.util.Try

case class WorkbookHelper(wb: Option[Workbook] = None,
                          file: Option[File] = None,
                          input: Option[InputStream] = None,
                          path: Option[String] = None,
                          dropFirstRows: Int = 0)
  extends Logger {

  require(List(wb, file, input, path).flatten.nonEmpty)

  val workbook: Workbook =
    List(wb, file.map(Excel.workbook), input.map(Excel.workbook), path.map(Excel.workbook))
      .flatten.headOption.get

  val sheets: Stream[Sheet] = (0 until workbook.getNumberOfSheets).map { sheetIndex =>
    val sheet = workbook.getSheetAt(sheetIndex)
    debug(s"Loading sheet: ${ sheet.getSheetName }")
    sheet
  }.toStream

  val sheetHelpers: Stream[SheetHelper] = sheets.map(s => SheetHelper.apply(s, dropFirstRows))
  val rows: Stream[Row] = sheetHelpers.flatMap(_.rows)
  val rowHelpers: Stream[RowHelper] = rows.map(RowHelper.apply)

  def rows[R](convert: (RowHelper => R)): Stream[R] = rowHelpers.map { rh =>
    Try(convert(rh)).toOption
  }.flatten
}
