package commons.money

trait DefaultCurrencyPrinter
  extends CurrencyPrinter {

  def w(value: Long): String = s"$value"
}
