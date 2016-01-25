package commons.money

import commons.money.CurrencyEnum.Currency

case class CurrencyAmount(val amount: Double,
                          val currency: Currency = CurrencyEnum.PLN) {

  def words = currency match {
    case cp: CurrencyPrinter => cp.words(amount)
    case _ => toString
  }

  override def toString = currency match {
    case cp: CurrencyPrinter => cp.digits(amount)
    case _ => f"$amount%.2f"
  }
}
