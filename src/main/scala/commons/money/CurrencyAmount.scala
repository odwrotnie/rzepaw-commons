package commons.money

import commons.money.CurrencyEnum.Currency
import commons.text.Pattern

case class CurrencyAmount(amount: Long,
                          currency: Currency = CurrencyEnum.PLN) {

  def plus(ca: CurrencyAmount): Option[CurrencyAmount] = {
    ca.to(currency).map(ca => currency.amount(amount + ca.amount))
  }

  def to(toCurrency: Currency): Option[CurrencyAmount] =
    EURCurrencyRate.calculate(currency, toCurrency)(amount) map { amount =>
      CurrencyAmount(amount, toCurrency)
    }
  def toEuro: Option[CurrencyAmount] = to(CurrencyEnum.EUR)

  def words: String = currency match {
    case cp: CurrencyPrinter => cp.words(amount)
    case _ => toString
  }

  override def toString = currency match {
    case cp: CurrencyPrinter => cp.digits(amount)
    case _ => if (currency.left) f"$currency$amount%.2f" else f"$amount%.2f $currency"
  }
}

object CurrencyAmount {
  def apply(currency: String)(value: Long): CurrencyAmount =
    CurrencyEnum.parse(currency).amount(value)
  implicit def stringToCurrencyAmound(s: String): CurrencyAmount = {
    val amount = math.round(Pattern.pickFirstDouble(s).get * 100)
    val currencySlug = Pattern.pickFirst("[A-Z]{3}".r)(s).get.toUpperCase
    val currency = CurrencyEnum.parse(currencySlug)
    currency.amount(amount)
  }
}
