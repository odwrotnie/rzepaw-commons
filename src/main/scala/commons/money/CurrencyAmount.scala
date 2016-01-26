package commons.money

import commons.money.CurrencyEnum.Currency
import commons.text.Pattern

case class CurrencyAmount(val amount: Double,
                          val currency: Currency = CurrencyEnum.PLN) {

  def to(toCurrency: Currency)(value: Double): Option[CurrencyAmount] =
    CurrencyRate.calculate(currency, toCurrency)(value).map { amount =>
      CurrencyAmount(amount, currency)
    }
  def toEuro(value: Double): Option[CurrencyAmount] = to(CurrencyEnum.EUR)(value)

  def words = currency match {
    case cp: CurrencyPrinter => cp.words(amount)
    case _ => toString
  }

  override def toString = currency match {
    case cp: CurrencyPrinter => cp.digits(amount)
    case _ => f"$amount%.2f"
  }
}

object CurrencyAmount {
  def apply(currency: String)(value: Double): Option[CurrencyAmount] =
    CurrencyEnum.parse(currency).map(_.amount(value))
  implicit def stringToCurrencyAmound(s: String): CurrencyAmount = {
    val amount = Pattern.pickFirstDouble(s).get
    val currencySlug = Pattern.pickFirst("[A-Z]{3}".r)(s).get.toUpperCase
    val currency = CurrencyEnum.parse(currencySlug)
    currency.get.amount(amount)
  }
}
