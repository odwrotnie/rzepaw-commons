package commons.data

case class JsonCell[AGG](row: String, col: String, value: AGG)
case class JsonRow[AGG](row: String, cells: List[JsonCell[AGG]])
case class JsonTable[AGG](rows: List[JsonRow[AGG]])

case class PivotTable[ROW, COL, AGG](rows: Iterable[ROW],
                                          cols: Iterable[COL],
                                          agg: (ROW, COL) => AGG) {

  val COL_SEPARATOR = " "
  val COL_HEAD_SEPARATOR = " | "
  val CELL_WIDTH = 13
  val CELL_PATTERN = s"%-${CELL_WIDTH}s"

  def rowToString(row: ROW): String = row.toString
  def colToString(col: COL): String = col.toString
  def aggToString(agg: AGG): String = agg.toString

  private def x: String = CELL_PATTERN format ""
  private def r(row: ROW): String = CELL_PATTERN format rowToString(row) take CELL_WIDTH
  private def c(col: COL): String = CELL_PATTERN format colToString(col) take CELL_WIDTH
  private def a(agg: AGG): String = CELL_PATTERN format aggToString(agg) take CELL_WIDTH

  def toJson = JsonTable(
    rows = rows.toList.map { row =>
      JsonRow(row.toString, cols.toList.map { col =>
        JsonCell(row.toString, col.toString, agg(row, col))
      })
    }
  )

  override def toString: String = {
    val _rows: Iterable[Iterable[AGG]] = rows.map { row =>
      cols.map { col =>
        agg(row, col)
      }
    }
    val _header: String = x + COL_HEAD_SEPARATOR + cols.map(c).mkString(COL_SEPARATOR)
    val _body: List[String] = _rows.zip(rows).map { case (agg, row) =>
      r(row) + COL_HEAD_SEPARATOR + agg.map(a).mkString(COL_SEPARATOR)
    }.toList
    (_header :: _body) mkString "\n"
  }
}
