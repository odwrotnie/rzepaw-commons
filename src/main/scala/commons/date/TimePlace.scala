package commons.date

object TimePlace extends Enumeration {
  case class V(name: String) extends Val(name)
  val PAST = V("past")
  val CURRENT = V("current")
  val FUTURE = V("future")
}
