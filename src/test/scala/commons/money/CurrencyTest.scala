package commons.money

import org.scalatest.FlatSpec
import CurrencyAmount._

/*
sbt "~rzepaw-commons/testOnly commons.money.CurrencyTest"
 */

class CurrencyTest
  extends FlatSpec {

  val value = 1234567800
  val words = "dwanaście milionów trzysta czterdzieści pięć tysięcy sześćset siedemdziesiąt osiem zł"

  s"33 grosze" should "be convertable" in {
    assert(CurrencyEnum.PLN.words(33) == "trzydzieści trzy gr")
  }

  s"minus 33 grosze" should "be convertable" in {
    assert(CurrencyEnum.PLN.words(-33) == "minus trzydzieści trzy gr")
  }

  val amount = "12.3PLN"
  s"$amount" should "be convertable" in {
    println("123.50PLN".toEuro)
    assert(CurrencyAmount.stringToCurrencyAmound(amount) == CurrencyAmount(1230, CurrencyEnum.PLN))
  }

  s"$value PLN" should s"be $words" in {
    val ca = CurrencyEnum.PLN.words(value)
    assert(ca.words == words)
  }

//  s"-$value PLN" should s"be $words" in {
//    val ca = CurrencyEnum.PLN.words(-value)
//    assert(ca.words == "minus " + words)
//  }
//
//  "Currency rate of PLN" should "be greater than 1" in {
//    assert(EURCurrencyRate.rate("PLN").isDefined)
//    assert(EURCurrencyRate.rate("PLN").get > 1)
//  }
//
//  "Currency rate of EUR" should "be equal" in {
//    val eur2eur = EURCurrencyRate.calculate("EUR", "EUR") _
//    assert(eur2eur(123).isDefined)
//    assert(eur2eur(123).get == 123)
//  }
//
//  "1 EUR in PLN" should "be greater than 1" in {
//    val eur2pln = EURCurrencyRate.calculate("EUR", "PLN") _
//    assert(eur2pln(100).isDefined)
//    assert(eur2pln(100).get > 2)
//  }
//
//  "1 USD in PLN" should "be greater than 1" in {
//    val usd2pln = EURCurrencyRate.calculate("USD", "PLN") _
//    assert(usd2pln(100).isDefined)
//    assert(usd2pln(100).get > 2)
//  }
//
//  "Decimals" should "work fine" in {
//    val w = CurrencyEnum.PLN.amount(1708).words
//    info(w)
//  }
}
