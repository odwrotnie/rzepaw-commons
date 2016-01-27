package commons.money

import commons.money.CurrencyEnum.Currency
import commons.text.Pattern

case class CurrencyAmount(val amount: Double,
                          val currency: Currency = CurrencyEnum.PLN) {

  def to(toCurrency: Currency): Option[CurrencyAmount] =
    EURCurrencyRate.calculate(currency, toCurrency)(amount).map { amount =>
      CurrencyAmount(amount, toCurrency)
    }
  def toEuro: Option[CurrencyAmount] = to(CurrencyEnum.EUR)

  def words = currency match {
    case cp: CurrencyPrinter => cp.words(amount)
    case _ => toString
  }

  override def toString = currency match {
    case cp: CurrencyPrinter => cp.digits(amount)
    case _ => if (currency.left) f"$currency$amount%.2f" else f"$amount%.2f $currency"
  }
}

object CurrencyAmount {
  def apply(currency: String)(value: Double): CurrencyAmount =
    CurrencyEnum.parse(currency).amount(value)
  implicit def stringToCurrencyAmound(s: String): CurrencyAmount = {
    val amount = Pattern.pickFirstDouble(s).get
    val currencySlug = Pattern.pickFirst("[A-Z]{3}".r)(s).get.toUpperCase
    val currency = CurrencyEnum.parse(currencySlug)
    currency.amount(amount)
  }
}
