package commons.money

object CurrencyEnum extends Enumeration {

  class Currency(val name: String, val slug: String,
                 val short: String, val p: String,
                 val left: Boolean = true) extends Val(slug) {
    def amount(a: Double): CurrencyAmount = CurrencyAmount(a, this)
    override def toString = short
  }

  private def short(slug: String): Currency = new Currency(slug, slug, slug, slug)

  val PLN = new Currency("Złoty", "PLN", "zł", "gr", left = false) with PLNCurrencyPrinter
  val EUR = new Currency("Euro", "EUR", "€", "¢")
  val USD = new Currency("US Dollar", "USD", "$", "¢")
  val GBP = new Currency("Pound", "GBP", "£", "p")
  val JPY = short("JPY")
  val BGN = short("BGN")
  val CZK = short("CZK")
  val DKK = short("DKK")
  val HUF = short("HUF")
  val RON = short("RON")
  val SEK = short("SEK")
  val CHF = short("CHF")
  val NOK = short("NOK")
  val HRK = short("HRK")
  val RUB = short("RUB")
  val TRY = short("TRY")
  val AUD = short("AUD")
  val BRL = short("BRL")
  val CAD = short("CAD")
  val CNY = short("CNY")
  val HKD = short("HKD")
  val IDR = short("IDR")
  val ILS = short("ILS")
  val INR = short("INR")
  val KRW = short("KRW")
  val MXN = short("MXN")
  val MYR = short("MYR")
  val NZD = short("NZD")
  val PHP = short("PHP")
  val SGD = short("SGD")
  val THB = short("THB")
  val ZAR = short("ZAR")

  lazy val all: Seq[Currency] = values.toSeq.map((v: Value) => v.asInstanceOf[Currency])
  def parse(currency: String): Option[Currency] =
    all.find(_.slug == currency.toUpperCase)
}
