package commons.map

import akka.actor.ActorSystem
import commons.logger.Logger
import org.scalatest.FunSuite
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._

import scala.concurrent.Future
import scala.util._

/*
sbt "~rzepawCommons/testOnly commons.map.GeolocationTest"
 */

class GeolocationTest
  extends FunSuite
  with Logger {

  val a = "Bydgoszcz"
  val b = "Berlin"

//  test("Distance test") {
//    println("Distance: " + Distance.between(a, b))
//    println("Location: " + Geolocation.calculate(a))
//    println("Location: " + Geolocation.calculate(b))
//  }

  test("Akka geolocation") {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val connectionFlow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] =
      Http().outgoingConnection("akka.io")
    val responseFuture: Future[HttpResponse] =
      Source.single(HttpRequest(uri = "/"))
        .via(connectionFlow)
        .runWith(Sink.head)

    responseFuture.andThen {
      case Success(_) => println("request succeded")
      case Failure(_) => println("request failed")
    }.andThen {
      case _ => system.terminate()
    }
  }
}
