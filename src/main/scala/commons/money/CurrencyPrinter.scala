package commons.money

trait CurrencyPrinter {

  def short: String
  def p: String
  def left: Boolean

  protected def w(v: Long): String

  def digits(value: Long): String = append("%.2f" format (value.toFloat / 100), short)

  def words(value: Long): String = {
    val before = w(value / 100)
    val after = w(value % 100)
    List(before, after)
      .zip(List(short, p))
      .filterNot(_._1.isEmpty)
      .flatMap(t => List(t._1, t._2))
      .mkString(" ")
      .trim
  }

  protected def append(s: String, cur: String) = {
    var l = List(s, cur)
    if (left) { l = l.reverse }
    l.mkString(" ")
  }
}
