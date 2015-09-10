package commons.map

import commons.logger.Logger
import dispatch._, Defaults._
import scala.concurrent.Await
import scala.concurrent.duration._

object Geolocation
  extends Logger {

  case class L(lat: Double, lng: Double)

  val h = host("maps.googleapis.com")
  val req = h / "maps" / "api" / "geocode" / "xml"
  def params(address: String) =
    req <<? Map("address" -> address,
      "language" -> "pl")

  private def geolocation(address: String): xml.Elem = {
    val http = new Http
    val futureDM: Future[xml.Elem] = http(params(address) OK as.xml.Elem)
    val dm = Await.result(futureDM, 15.seconds)
    http.shutdown()
    dm
  }

  def calculate(address: String): Option[L] = try {
    val xml = geolocation(address)
    val l = L((xml \\ "location" \ "lat" text).toDouble,
      (xml \\ "location" \ "lng" text).toDouble)
    Some(l)
  } catch {
    case _: Throwable => None
  }

  private def geocode(address: String): xml.Elem = {
    val http = new Http
    val futureGC: Future[xml.Elem] = http(params(address) OK as.xml.Elem)
    val dm = Await.result(futureGC, 15.seconds)
    http.shutdown()
    dm
  }
}
