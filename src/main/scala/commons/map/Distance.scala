package commons.map

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.http.scaladsl.unmarshalling.Unmarshal
import commons.logger.Logger
import scala.concurrent.Future
import scala.util._
import scala.xml._

object Distance
  extends Logger
  with ScalaXmlSupport {

  val ORIGINS = "origins"
  val DESTINATIONS = "destinations"
  val URI = "http://maps.googleapis.com/maps/api/distancematrix/xml"

  case class D(seconds: Long, meters: Long) {
    lazy val kilometers: Float = meters.toFloat / 1000
    lazy val hours: Float = seconds.toFloat / 3600
    var durationText: String = seconds.toString
    var distanceText: String = meters.toString
    override def toString = f"$kilometers%2.2fKm|$hours%2.2fh"
  }

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  private def distanceMatrix(origins: String, destinations: String): Future[NodeSeq] = {
    debug(s"Asking Google API for distance between $origins and $destinations")
    val query = Query("origins" -> origins,
      "destinations" -> destinations,
      "language" -> "pl")
    val uri = Uri(URI).withQuery(query)
    for {
      response <- Http().singleRequest(HttpRequest(uri = uri))
      entity <- Unmarshal(response.entity).to[NodeSeq]
    } yield entity
  }

  def between(origin: String, destination: String): Future[D] = distanceMatrix(origin, destination).map { xml =>
    require(origin.nonEmpty && destination.nonEmpty, "The origin or destination string is empty")
    val d = D((xml \\ "duration" \ "value" text).toLong,
      (xml \\ "distance" \ "value" text).toLong)
    d.durationText = (xml \\ "duration" \ "text" text)
    d.distanceText = (xml \\ "distance" \ "text" text)
    d
  }
}
