package commons.settings

object JNDIOrProperties {

  def get(path: String*): Option[String] = {
    val results: List[String] = List(
      JNDI.get(path.mkString("/")),
      ResourceProperties(s"/${ path.head }.properties").get(path.tail.mkString("."))
    ).flatten
    results.headOption
  }
}
