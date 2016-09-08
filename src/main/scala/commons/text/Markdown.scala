package commons.text

import org.pegdown.PegDownProcessor

//import com.tristanhunt.knockoff.DefaultDiscounter._

object Markdown {

  val pegDown = new PegDownProcessor()

  def toHtmlString(markdownString: String) =
    pegDown.markdownToHtml(markdownString)
}
