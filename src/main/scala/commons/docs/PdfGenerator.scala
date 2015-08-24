package commons.docs

import io.github.cloudify.scala.spdf._

object PdfGenerator {

  def pdf(landscape: Boolean = false) = Pdf(new PdfConfig {
    orientation := (landscape match {
      case true => Landscape
      case _ => Portrait
    })
    pageSize := "A4"
    marginTop := "0"
    marginBottom := "0"
    marginLeft := "0"
    marginRight := "0"
  })
}
