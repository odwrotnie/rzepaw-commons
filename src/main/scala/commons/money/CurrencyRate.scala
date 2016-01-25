package commons.money

import scala.util.Try
import scala.xml.{Node, XML}

object CurrencyRate {

  val xml = XML.load("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml")

  def eurTo(currency: String): Option[Double] = {
    val nodes = xml \ "Cube" \ "Cube" \ "Cube"
    val rate: Option[String] = nodes.filter(node => (node \ "@currency" text) == "PLN").map { node =>
      node \ "@rate" text
    }.headOption
    rate.flatMap(s => Try(s.toDouble).toOption)
  }
}
