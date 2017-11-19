package commons.docs

import com.typesafe.scalalogging.LazyLogging
import org.apache.poi.ss.usermodel.Cell

case class CellHelper(cell: Cell, sh: SheetHelper)
  extends LazyLogging {

  def above: Option[CellHelper] = if (cell.getRowIndex > 0)
    Some(CellHelper(sh.cell(cell.getRowIndex - 1, cell.getColumnIndex), sh))
  else
    None

  def below: Option[CellHelper] = if (cell.getRowIndex < sh.sheet.getLastRowNum)
    Some(CellHelper(sh.cell(cell.getRowIndex + 1, cell.getColumnIndex), sh))
  else
    None

  def left: Option[CellHelper] = if (cell.getColumnIndex > 0)
    Some(CellHelper(sh.cell(cell.getRowIndex, cell.getColumnIndex - 1), sh))
  else
    None

  def right: Option[CellHelper] = if (cell.getColumnIndex < cell.getRow.getLastCellNum)
    Some(CellHelper(sh.cell(cell.getRowIndex, cell.getColumnIndex + 1), sh))
  else
    None
}
