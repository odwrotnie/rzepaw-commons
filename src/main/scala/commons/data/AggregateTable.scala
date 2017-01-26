package commons.data

abstract class AggregateTable[ROW, COL, AGG <: AnyVal](val rows: Iterable[ROW],
                                                       val cols: Iterable[COL]) {

  def _rowcol(r: ROW, c: COL): AGG
  def _agg(cols: Iterable[AGG]): AGG

  def rowsAgg: List[AGG] = rows.toList.map(row => _agg(cols.map(col => rowcol(row, col))))
  def colsAgg: List[AGG] = cols.toList.map(col => _agg(rows.map(row => rowcol(row, col))))

  val COL_SEPARATOR = " "
  val COL_HEAD_SEPARATOR = " | "
  val CELL_WIDTH = 13
  val CELL_PATTERN = s"%-${CELL_WIDTH}s"

  private val rowcolCacheMap = collection.mutable.HashMap[(ROW, COL), AGG]()
  def rowcol(r: ROW, c: COL): AGG = rowcolCacheMap.getOrElse((r, c), {
    val computed = _rowcol(r, c)
    rowcolCacheMap += (r, c) -> computed
    computed
  })
  //  lazy val aggList = for {
  //    row <- rows
  //    col <- cols
  //  } yield rowcol(row, col)

  lazy val _rows: Map[String, List[AGG]] = rows.zip(rowsAgg).toList.map {
    case (row, agg) =>
      rowToString(row) -> (cols.toList.map { col =>
        rowcol(row, col)
      } :+ agg)
  }.toMap + ("Agg." -> colsAgg)

  def toString(a: Any): String = a match {
    case r: ROW => rowToString(r)
    case c: COL => colToString(c)
    case a: AGG => aggToString(a)
    case x => x.toString
  }
  private def rowToString(row: ROW): String = row.toString
  private def colToString(col: COL): String = col.toString
  private def aggToString(agg: AGG): String = agg.toString
  private def toCellString(v: Any): String = CELL_PATTERN format v
  override def toString: String = {
    val _header: String = toCellString("") + COL_HEAD_SEPARATOR + cols.map(toCellString).mkString(COL_SEPARATOR)
    val _body: List[String] = _rows.map {
      case (row, agg) =>
        toCellString(row) + COL_HEAD_SEPARATOR + agg.map(toCellString).mkString(COL_SEPARATOR)
    }.toList
    (_header :: _body) mkString "\n"
  }
}
