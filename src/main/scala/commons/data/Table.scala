package commons.data

case class Table[T](headers: List[String],
                    rows: List[List[T]]) {

  val LINE_DELIMITER = "\n"
  val ROW_DELIMITER = ","

  def print(delimiter: String): String = {
    headers.mkString(delimiter) +
      LINE_DELIMITER +
      rows.map(row => row.mkString(delimiter)).mkString(LINE_DELIMITER)
  }

  override def toString = print(ROW_DELIMITER)
}

object Table {

  def apply[T](headers: List[String],
               rows: List[T]*): Table[T] = {
    Table(headers, rows.toList)
  }
}
