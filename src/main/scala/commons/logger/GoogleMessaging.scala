package commons.logger

import spray.client.pipelining._
import spray.http.Uri.Path
import spray.http._
import spray.httpx.{PipelineException, SprayJsonSupport, UnsuccessfulResponseException}

import akka.actor.ActorSystem

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.parsing.json.JSONObject

case class GoogleMessaging(title: String, key: String)
  extends Logger {

  implicit val system = ActorSystem()
  import system.dispatcher

  def notify(message: String): Unit = {
    val URI = Uri("https://gcm-http.googleapis.com")
    val pipeline = (
      addHeader("Authorization", s"key=$key")
        ~> sendReceive
        ~> unmarshal[String])
    val uri = URI.withPath(Path / "gcm" / "send")
    val json = JSONObject(Map(
      "notification" -> JSONObject(Map(
        "sound" -> "default",
        "badge" -> "1",
        "title" -> title,
        "body" -> message,
        "icon" -> "ic_stat_ic_notification"
      )),
      "to" -> "/topics/global"
    ))
    val post = Post(uri, HttpEntity(MediaTypes.`application/json`, json.toString()))
    // debug(s"Google Messaging POST: $post")
    val ppl: Future[String] = pipeline(post)
    //    val futureResult = ppl.map { t: String =>
    //      info(s"NMG API Token: $t")
    //      Some(t)
    //    }
    val response = Await.result(ppl, Duration.Inf)
    info(s"Google messaging response: $response")
  }
}
