package commons.logger

import spray.client.pipelining._
import spray.http.Uri.Path
import spray.http.{HttpRequest, HttpResponse, Uri}
import spray.httpx.{PipelineException, SprayJsonSupport, UnsuccessfulResponseException}

import akka.actor.ActorSystem

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.parsing.json.JSONObject

case class GoogleMessaging(key: String)
  extends Logger {

  implicit val system = ActorSystem()
  import system.dispatcher

  val URI = Uri("https://gcm-http.googleapis.com")
  val pipeline = (
    addHeader("Authorization", s"key=$key")
      ~> addHeader("Content-Type", "application/json")
      ~> sendReceive
      ~> unmarshal[String])
  val uri = URI.withPath(Path / "gcm" / "send")
  val json = JSONObject(Map(
    "notification" -> JSONObject(Map(
      "sound" -> "default",
      "badge" -> "1",
      "title" -> "name",
      "body" -> "message",
      "icon" -> "ic_stat_ic_notification"
    )),
    "to" -> "/topics/global"
  ))
  val post = Post(uri, json.toString())
  debug(s"Google Messaging POST: $post")
  val ppl: Future[String] = pipeline(post)
  //    val futureResult = ppl.map { t: String =>
  //      info(s"NMG API Token: $t")
  //      Some(t)
  //    }
  val response = Await.result(ppl, Duration.Inf)
  info("RESPONSE: " + response)
}
