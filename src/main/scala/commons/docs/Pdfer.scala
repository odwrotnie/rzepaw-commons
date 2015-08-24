package commons.docs

import java.io._

case class Pdfer[OS <: OutputStream](html: scala.xml.NodeSeq, out: OS, landscape: Boolean = false) {

//  val worker = XMLWorkerHelper.getInstance()
//  val cssResolver = worker.getDefaultCssResolver(true)
//
//  val htmlContext = {
//    val hc = new HtmlPipelineContext(null)
//    hc.setTagFactory(Tags.getHtmlTagProcessorFactory())
//    hc
//  }
//
//  val document = {
//    val d = new Document()
//    d.addAuthor("Wext")
//    d.addCreationDate()
//    d.addProducer()
//    d.addCreator("Wext")
//    d.addTitle("Wext")
//    d.setPageSize(PageSize.A4)
//    d
//  }
//
//  val writer = PdfWriter.getInstance(document, out)
//
//  val pipeline = new CssResolverPipeline(cssResolver,
//    new HtmlPipeline(htmlContext,
//      new PdfWriterPipeline(document, writer)))
//
//  document.open()
//
//  worker.parseXHtml(writer, document, new StringReader(html.toString))
//  document.close()
//  writer.close()

  PdfGenerator.pdf(landscape).run(html.toString, out)
}
