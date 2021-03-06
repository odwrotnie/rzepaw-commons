package commons.money

trait PLNCurrencyPrinter
  extends CurrencyPrinter {

  def w(v: Long): String = {

    val numbers: List[Int] = math.abs(v).toString
      .reverse
      .grouped(3)
      .toList
      .reverse
      .map(_.reverse.mkString("").toInt)
      .reverse

    val words = numbers.zipWithIndex.reverse.flatMap(t => List(lessThan1000Words(t._1, t._2))).mkString(" ").trim

    if (v < 0) s"minus $words" else words
  }

  private def lessThan1000Words(number: Long, thousandPow: Int) = {

    assert(number >= 0 && number < 1000, "Number should contain 3 digits and be positive (%s)" format number)

    val value = (number % 1000 / 100, number % 100 / 10, number % 10)

    val ones = value match {
      case (_, 1, _) => ""
      case (_, _, 0) => ""
      case (_, _, 1) => "jeden"
      case (_, _, 2) => "dwa"
      case (_, _, 3) => "trzy"
      case (_, _, 4) => "cztery"
      case (_, _, 5) => "pięć"
      case (_, _, 6) => "sześć"
      case (_, _, 7) => "siedem"
      case (_, _, 8) => "osiem"
      case (_, _, 9) => "dziewięć"
    }

    val tens = value match {
      case (_, 0, _) => ""
      case (_, 1, 0) => "dziesięć"
      case (_, 1, 1) => "jedenaście"
      case (_, 1, 2) => "dwanaście"
      case (_, 1, 3) => "trzynaście"
      case (_, 1, 4) => "czternaście"
      case (_, 1, 5) => "piętnaście"
      case (_, 1, 6) => "szesnaście"
      case (_, 1, 7) => "siedemnaście"
      case (_, 1, 8) => "osiemnaście"
      case (_, 1, 9) => "dziewiętnaście"
      case (_, 2, _) => "dwadzieścia"
      case (_, 3, _) => "trzydzieści"
      case (_, 4, _) => "czterdzieści"
      case (_, 5, _) => "pięćdziesiąt"
      case (_, 6, _) => "sześćdziesiąt"
      case (_, 7, _) => "siedemdziesiąt"
      case (_, 8, _) => "osiemdziesiąt"
      case (_, 9, _) => "dziewięćdziesiąt"
    }

    val hundreds = value match {
      case (0, _, _) => ""
      case (1, _, _) => "sto"
      case (2, _, _) => "dwieście"
      case (3, _, _) => "trzysta"
      case (4, _, _) => "czterysta"
      case (5, _, _) => "pięćset"
      case (6, _, _) => "sześćset"
      case (7, _, _) => "siedemset"
      case (8, _, _) => "osiemset"
      case (9, _, _) => "dziewięćset"
    }

    val numerals = Map(
      0 -> List("", "", "", "", "", "", "", ""),
      1 -> List("", "tysiąc", "milion", "miliard", "bilion", "biliard", "trylion", "tryliard"),
      2 -> List("", "tysiące", "miliony", "miliardy", "biliony", "biliardy", "tryliony", "tryliardy"),
      3 -> List("", "tysięcy", "milionów", "miliardów", "bilionów", "biliardów", "trylionów", "tryliardów")
    )

    val numeral = value match {
      case (0, 0, 0) => numerals(0)(thousandPow)
      case (0, 0, 1) => numerals(1)(thousandPow)
      case (_, 1, _) => numerals(3)(thousandPow)
      case (_, _, 2) => numerals(2)(thousandPow)
      case (_, _, 3) => numerals(2)(thousandPow)
      case (_, _, 4) => numerals(2)(thousandPow)
      case (_, _, _) => numerals(3)(thousandPow)
    }

    List(hundreds, tens, ones, numeral).filterNot(_.isEmpty).mkString(" ").trim
  }
}
