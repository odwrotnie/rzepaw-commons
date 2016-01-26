package commons.money

import org.scalatest.FlatSpec

/*
sbt "~rzepawCommons/testOnly commons.money.CurrencyTest"
 */

class CurrencyTest
  extends FlatSpec {

  val value = 12345678f
  val words = "dwanaście milionów trzysta czterdzieści pięć tysięcy sześćset siedemdziesiąt osiem zł"

  val amount = "12.3PLN"
  s"$amount" should "be convertable" in {
    assert(CurrencyAmount.stringToCurrencyAmound(amount) == CurrencyAmount(12.3, CurrencyEnum.USD))
  }

  s"$value PLN" should s"be $words" in {
    val ca = CurrencyAmount(value, CurrencyEnum.PLN)
    assert(ca.words == words)
  }

  "Currency rate of PLN" should "be greater than 1" in {
    assert(EURCurrencyRate.rate("PLN").isDefined)
    assert(EURCurrencyRate.rate("PLN").get > 1)
  }

  "Currency rate of EUR" should "be equal" in {
    val eur2eur = EURCurrencyRate.calculate("EUR", "EUR") _
    assert(eur2eur(1.23).isDefined)
    assert(eur2eur(1.23).get == 1.23)
  }

  "1 EUR in PLN" should "should be greater than 1" in {
    val eur2pln = EURCurrencyRate.calculate("EUR", "PLN") _
    assert(eur2pln(1).isDefined)
    assert(eur2pln(1).get > 2)
  }

  "1 USD in PLN" should "should be greater than 1" in {
    val usd2pln = EURCurrencyRate.calculate("USD", "PLN") _
    assert(usd2pln(1).isDefined)
    assert(usd2pln(1).get > 2)
  }
}
