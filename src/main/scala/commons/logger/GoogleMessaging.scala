package commons.logger

import spray.client.pipelining._
import spray.http.Uri.Path
import spray.http._
import spray.httpx.{PipelineException, SprayJsonSupport, UnsuccessfulResponseException}

import akka.actor.ActorSystem

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}
import scala.util.parsing.json.JSONObject

/**
 * Manual: https://developers.google.com/cloud-messaging/downstream
 * Json: https://developers.google.com/cloud-messaging/http-server-ref
 * @param title
 * @param key
 */
case class GoogleMessaging(title: String, key: String)
  extends Logger {

  implicit val system = ActorSystem()
  import system.dispatcher

  val URI = Uri("https://gcm-http.googleapis.com")
  val pipeline = (
    addHeader("Authorization", s"key=$key")
      ~> sendReceive
      ~> unmarshal[String])
  val uri = URI.withPath(Path / "gcm" / "send")

  def notify(message: String, href: String): Future[String] = {
    val json = JSONObject(Map(
      "data" -> JSONObject(Map(
        "name" -> title,
        "message" -> message,
        "href" -> href
      )),
      "to" -> "/topics/global"
    ))

    val post = Post(uri,
      HttpEntity(
        MediaTypes.`application/json`,
        HttpData(json.toString(),
          HttpCharset.custom("UTF-8").get)))

    val response: Future[String] = pipeline(post)
    response onComplete {
      case Success(s) => debug(s"Message successfuly sent - $s")
      case Failure(t) => error(s"Message sending failure - ${ t.getMessage }")
    }
    response
  }
}
