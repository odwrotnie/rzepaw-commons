package commons.money

trait DefaultCurrencyPrinter
  extends CurrencyPrinter {

  def w(v: Long): String = s"$v"
}
