package commons.map

import commons.logger.Logger
import dispatch._, Defaults._
import scala.concurrent.Await
import scala.concurrent.duration._

object Distance
  extends Logger {

  case class D(seconds: Long, meters: Long) {
    lazy val kilometers: Float = meters.toFloat / 1000
    lazy val hours: Float = seconds.toFloat / 3600
    var durationText: String = seconds.toString
    var distanceText: String = meters.toString
    override def toString = f"$kilometers%2.2fKm|$hours%2.2fh"
  }

  val h = host("maps.googleapis.com")
  val req = h / "maps" / "api" / "distancematrix" / "xml"
  def params(origins: String, destinations: String) =
    req <<? Map("origins" -> origins,
      "destinations" -> destinations,
      "language" -> "pl")

  private def distanceMatrix(origins: String, destinations: String): xml.Elem = {
    val http = new Http
    val futureDM: Future[xml.Elem] = http(params(origins, destinations) OK as.xml.Elem)
    val dm = Await.result(futureDM, 15.seconds)
    http.shutdown()
    dm
  }

  def between(origin: String, destination: String): Option[D] = try {
    if (origin.isEmpty || destination.isEmpty)
      error("The origin or destination string is empty")
    val xml = distanceMatrix(origin, destination)
    val d = D((xml \\ "duration" \ "value" text).toLong,
      (xml \\ "distance" \ "value" text).toLong)
    d.durationText = (xml \\ "duration" \ "text" text)
    d.distanceText = (xml \\ "distance" \ "text" text)
    Some(d)
  } catch {
    case _: Throwable => None
  }
}
