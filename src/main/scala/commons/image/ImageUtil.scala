//package commons.image
//
////import javax.imageio._
////import java.io._
////import java.awt._
////import java.awt.image._
////import net.liftweb.common._
////import net.liftmodules.imaging._
//
//import javax.imageio._
//import java.io._
//import java.awt._
//import java.awt.image._
//import net.liftweb.common._
//import net.liftmodules.imaging._
//import java.io.{ByteArrayInputStream, File, ByteArrayOutputStream}
//import commons.Base64
//
//object ImageUtil extends Logger {
//
//  val MIN_SIZE = 50
//
//  val RESIZED_FORMAT = ImageOutFormat.png
//  val RESIZED_MIME = "image/png"
//
//  def toHtml(mime: String, bytes: Array[Byte]) =
//      <img src={ "data:%s;base64,%s" format
//      (mime, Base64.encode(bytes)) } />
//
//  def toHtmlFit(bytes: Array[Byte], w: Int, h: Int) =
//      <img src={ "data:%s;base64,%s" format
//      (RESIZED_MIME, Base64.encode(resizedBytes(bytes, w, h))) } />
//
//  def bufferedImage(bytes: Array[Byte]) = {
//    val imageByteArrayInputStream = new ByteArrayInputStream(bytes)
//    val bufferedImage = ImageResizer.getImageFromStream(imageByteArrayInputStream).image
//    imageByteArrayInputStream.close
//    bufferedImage
//  }
//
//  def bytes(bufferedImage: BufferedImage) = {
//    ImageResizer.imageToBytes(RESIZED_FORMAT, bufferedImage, 1)
//  }
//
//  def resizedBytes(imageBytes: Array[Byte], w: Int, h: Int) = {
//    val bi = bufferedImage(imageBytes)
//    val resized = resizeFit(bi, w, h)
//    bytes(resized)
//  }
//
//  def dummyImage(width:Int, height:Int, text:Option[String] = None) = {
//    val w = math.max(width, MIN_SIZE)
//    val h = math.max(height, MIN_SIZE)
//    val image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
//
//    val g = image.getGraphics
//
//    g.setColor(Color.WHITE)
//    g.fillRect(0, 0, w, h);
//
//    g.setColor(Color.BLACK)
//    g.drawRect(0, 0, w - 1, h - 1)
//
//    text match {
//      case Some(str) => {
//        val fm = g.getFontMetrics
//        val textX = (w - fm.stringWidth(str)) / 2
//        val textY = (fm.getAscent() + (h - (fm.getAscent() + fm.getDescent())) / 2)
//        g.drawString(str, textX, textY)
//      }
//      case _ => {}
//    }
//
//    g.dispose
//
//    image
//  }
//
//  def resizeFit(originalImage:BufferedImage, w:Int, h:Int) = {
//    val originalW = originalImage.getWidth(null)
//    val originalH = originalImage.getHeight(null)
//
//    val scaleRatio:Double = math.max(w.toDouble / originalW, h.toDouble / originalH)
//    val scaledW = math.round(scaleRatio * originalW).toInt
//    val scaledH = math.round(scaleRatio * originalH).toInt
//    val scaledImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
//
//    val xOffset = (w - scaledW) / 2
//    val yOffset = (h - scaledH) / 2
//
//    val graphics2D = scaledImage.createGraphics
//    graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
//    // val xform = AffineTransform.getScaleInstance(scaledW, scaledH)
//    // graphics2D.drawImage(originalImage, xform, null)
//    graphics2D.drawImage(originalImage, xOffset, yOffset, scaledW, scaledH, null)
//    graphics2D.dispose()
//
//    scaledImage
//  }
//
//  def resizeMax(originalImage:BufferedImage, w:Int, h:Int) = {
//    val width = originalImage.getWidth()
//    val height = originalImage.getHeight()
//    if (w <= width || h <= height) {
//      ImageResizer.max(Full(ImageOrientation.ok), originalImage, w, h)
//    } else {
//      val image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
//      val g = image.getGraphics
//      g.drawImage(originalImage, w/2 - width/2, h/2 - height/2, null)
//      g.dispose
//      image
//    }
//  }
//}
