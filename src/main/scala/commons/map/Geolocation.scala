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

object Geolocation
  extends Logger
    with ScalaXmlSupport {

  case class L(lat: Double, lng: Double)

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val ADDRESS = "address"
  val URI = "http://maps.googleapis.com/maps/api/geocode/xml"

  def location(address: String): Future[L] = geocode(address).map { xml =>
    val location = xml \\ "geometry" \\ "location"
    val lat = (location \ "lat").text.toDouble
    val lng = (location \ "lng").text.toDouble
    L(lat, lng)
  }

  private def geocode(address: String): Future[NodeSeq] = {
    val uri = Uri(URI).withQuery(Query(ADDRESS -> address))
    for {
      response <- Http().singleRequest(HttpRequest(uri = uri))
      entity <- Unmarshal(response.entity).to[NodeSeq]
    } yield entity
  }
}
