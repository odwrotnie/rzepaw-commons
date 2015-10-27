package commons.data

case class Table[T](headers: List[String],
                    rows: List[List[T]]) {

  val LINE_DELIMITER = "\n"
  val ROW_DELIMITER = ","

  def print(rowToString: List[Any] => String): String =
    (headers :: rows).map(row => rowToString(headers)).mkString(LINE_DELIMITER)

  def googleVisualizationArray: List[List[Any]] = headers :: rows
  def googleVisualizationArrayString: String = print(_.mkString("[", ",", "]"))

  override def toString = print(_.mkString(ROW_DELIMITER))
}

object Table {

  def apply[T](headers: List[String],
               rows: List[T]*): Table[T] = {
    Table(headers, rows.toList)
  }
}
