package commons.text

import java.net.URLEncoder
import java.text.Normalizer
import java.text.Normalizer.Form

import scala.util.Random

object Slugify {

  val unpolish = List("ą" -> "a", "ć" -> "c", "ę" -> "e", "ł" -> "l",
    "ń" -> "n", "ó" -> "o", "ś" -> "s", "ź" -> "z", "ż" -> "z")

  def apply(input: String) = {
    var slug = input.trim
    slug = slug.toLowerCase()
    unpolish.foreach(unp => slug = slug.replace(unp._1, unp._2))
    // slug = Normalizer.normalize(input, Form.NFD)
    slug = slug.replaceAll("\\W+", " ").trim.replaceAll("\\W", "-")
    slug = URLEncoder.encode(slug, "UTF-8")
    slug
  }

  private def normalize(input: String) = {
    var slug = Normalizer.normalize(input, Form.NFD)
    slug = slug.replaceAll("\\W", "-")
    slug
  }

  def random(n: Int) =
    apply(Random.alphanumeric take n mkString)

  //  def randomUnique(n: Int, singleton: MetaMapper[_], mappedString: MappedString[_]) = {
  //    var slug = ""
  //    do {
  //      val slug = Slugify.random(n)
  //    } while (!singleton.findAll(By(mappedString, slug)).isEmpty)
  //  }
}
