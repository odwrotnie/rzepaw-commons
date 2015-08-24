package commons.text

//import com.tristanhunt.knockoff.DefaultDiscounter._

object Markdown {

  def toHtml(markdownString: String) =
    //toXHTML(knockoff(markdownString))
  <h1>{ markdownString }</h1>
}
