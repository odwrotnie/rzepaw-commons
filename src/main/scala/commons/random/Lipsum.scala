package commons.random

import net._01001111.text._

import scala.util.Random

object Lipsum {

  val loremIpsum = new LoremIpsum()

  private def capitalize(s: String) =
    s(0).toUpper + s.substring(1, s.length).toLowerCase

  def generate(length: Int): String = capitalize(loremIpsum.words(length))

  def generate(min: Int, max: Int): String = {
    val r = Random.nextInt(max - min + 1)
    generate(r + min)
  }

  def email = {
    def word = loremIpsum.words(1)
    def domain = Rand.one(Seq("pl", "com", "net", "de", "me"))
    s"$word@$word.$domain"
  }

  def digits(n: Int) = (1 to n).map(Random.nextInt).mkString("")
}
