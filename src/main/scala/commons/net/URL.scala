package commons.net

object URL {

  val AND = "&"
  val IS = "="

  def paramsMap(query: String): Map[String, String] = {
    query.split("&").map((kv: String) => {
      val pair = kv.split("=")
      pair(0) -> pair(1)
    }).toMap
  }

  def paramsString(params: Map[String, String]): String = {
    params.map(kv => kv._1 + IS + kv._2).mkString(AND)
  }
}
