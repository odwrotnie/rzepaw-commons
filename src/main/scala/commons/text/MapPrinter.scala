package commons.text

object MapPrinter {

  def print(map: Map[_, _]): String = {
    val valuesString = toStringLines(map).mkString("\n")
    "Map (\n" + valuesString + "\n)"
  }

  def toStringLines(map: Map[_, _]) = map
    .flatMap{ case (k, v) => keyValueToString(k, v)}
    .map(indentLine)

  def keyValueToString[K, V](key: K, value: V): Iterable[String] = value match {
    case v: Map[_, _] => Iterable(key + " -> Map (") ++ toStringLines(v) ++ Iterable(")")
    case x => Iterable(key + " -> " + x.toString)
  }

  def indentLine(line: String): String = "\t" + line
}
