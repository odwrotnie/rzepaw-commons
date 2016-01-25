package commons.money

trait CurrencyPrinter {

  def short: String
  def p: String
  def left: Boolean

  protected def w(v: Long): String

  def digits(value: Double) = append("%.2f" format value, short)

  def words(value: Double) = {
    val before = w(value.toLong)
    val after = w(((value * 100.0f) % 100).toLong)
    List(before, after).filterNot(_.isEmpty).zip(List(short, p)).
      flatMap(t => List(t._1, t._2)).mkString(" ").trim
  }

  protected def append(s: String, cur: String) = {
    var l = List(s, cur)
    if (left) { l = l.reverse }
    l.mkString(" ")
  }
}
