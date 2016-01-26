package commons.money

import commons.data.IntervalRefreshValue
import commons.money.CurrencyEnum.Currency

import scala.util.Try
import scala.xml.{Elem, Node, XML}

object EURCurrencyRate {

  val BASE_CURRENCY = CurrencyEnum.EUR

  lazy val xml =
    new IntervalRefreshValue[Elem](XML.load("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml"))

  def rate(currency: String): Option[Double] = {
    val nodes = xml.get \ "Cube" \ "Cube" \ "Cube"
    val rate: Option[String] = nodes.filter(node => (node \ "@currency" text) == currency).map { node =>
      node \ "@rate" text
    }.headOption
    rate.flatMap(s => Try(s.toDouble).toOption)
  }

  def calculate(from: String, to: String)(value: Double): Option[Double] = for {
    fromRate <- if (from == BASE_CURRENCY.slug) Some(1d) else rate(from)
    toRate <- if (to == BASE_CURRENCY.slug) Some(1d) else rate(to)
  } yield {
    val euro = value / fromRate
    toRate * euro
  }

  def calculate(from: Currency, to: Currency)(value: Double): Option[Double] =
    calculate(from.slug, to.slug)(value)
}
