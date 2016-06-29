//package commons.text
//
//implicit class PrettyPrintMap[K, V](val map: Map[K, V]) {
//  def prettyPrint: PrettyPrintMap[K, V] = this
//
//  override def toString: String = {
//    val valuesString = toStringLines.mkString("\n")
//
//    "Map (\n" + valuesString + "\n)"
//  }
//
//  def toStringLines = {
//    map
//      .flatMap{ case (k, v) => keyValueToString(k, v)}
//      .map(indentLine(_))
//  }
//
//  def keyValueToString(key: K, value: V): Iterable[String] = {
//    value match {
//      case v: Map[_, _] => Iterable(key + " -> Map (") ++ v.prettyPrint.toStringLines ++ Iterable(")")
//      case x => Iterable(key + " -> " + x.toString)
//    }
//  }
//
//  def indentLine(line: String): String = {
//    "\t" + line
//  }
//}
