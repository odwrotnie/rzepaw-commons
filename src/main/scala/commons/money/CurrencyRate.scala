package commons.money

import commons.money.CurrencyEnum.Currency

import scala.util.Try
import scala.xml.{Node, XML}

object CurrencyRate {

  val BASE_CURRENCY = CurrencyEnum.EUR

  val xml = XML.load("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml")

  def euroIn(currency: String): Option[Double] = {
    val nodes = xml \ "Cube" \ "Cube" \ "Cube"
    val rate: Option[String] = nodes.filter(node => (node \ "@currency" text) == currency).map { node =>
      node \ "@rate" text
    }.headOption
    rate.flatMap(s => Try(s.toDouble).toOption)
  }

  def calculate(from: String, to: String)(value: Double): Option[Double] = for {
    fromRate <- if (from == BASE_CURRENCY.slug) Some(1d) else euroIn(from)
    toRate <- if (to == BASE_CURRENCY.slug) Some(1d) else euroIn(to)
  } yield {
    val euro = value / fromRate
    toRate * euro
  }

  def calculate(from: Currency, to: Currency)(value: Double): Option[Double] =
    calculate(from.slug, to.slug)(value)
}
