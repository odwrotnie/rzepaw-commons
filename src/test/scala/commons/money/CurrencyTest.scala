package commons.money

import money.Currency
import org.scalatest.FlatSpec

class CurrencyTest
  extends FlatSpec {

  val value = 5678.90f
  val words = "pięć tysięcy sześćset siedemdziesiąt osiem zł dziewięćdziesiąt gr"

//  val value = 12345678.90f
//  val words = "dwanaście milionów trzysta czterdzieści pięć tysięcy sześćset siedemdziesiąt osiem zł dziewięćdziesiąt gr"

//  s"$value PLN" should s"be $words" in {
//    assert(Currency.PLN.words(value) == words)
//  }

  "Currency rate of PLN" should "be greater than 1" in {
    assert(CurrencyRate.euroIn("PLN").isDefined)
    assert(CurrencyRate.euroIn("PLN").get > 1)
  }

  "Currency rate of EUR" should "be equal" in {
    println(CurrencyRate.calculate("EUR", "EUR")(1.23))
    assert(CurrencyRate.calculate("EUR", "EUR")(1.23).isDefined)
    assert(CurrencyRate.calculate("EUR", "EUR")(1.23).get == 1.23)
  }
}
