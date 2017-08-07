package commons.docs

import com.typesafe.scalalogging.LazyLogging

import scala.util.Try

abstract class WorkbookRowsConverter[T](wb: WorkbookHelper)
  extends LazyLogging {

  def convertRow(sh: SheetHelper, rh: RowHelper): T

  def stream: Stream[T] = for {
    sh <- wb.sheetHelpers
    rh <- sh.rowHelpers
  } yield convert(sh, rh)

  def streamSafe: Stream[T] = (for {
    sh <- wb.sheetHelpers
    rh <- sh.rowHelpers
  } yield convertSafe(sh, rh)).flatten

  private def convert(sh: SheetHelper, rh: RowHelper): T = {
    logger.debug(s"Converting $rh")
    convertRow(sh, rh)
  }

  private def convertSafe(sh: SheetHelper, rh: RowHelper): Option[T] = try {
    Some(convert(sh, rh))
  } catch {
    case x: Throwable =>
      logger.warn(s"Convertion of row $rh in sheet $sh error: ${ x.getMessage }")
      None
  }
}
