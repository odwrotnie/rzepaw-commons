package commons.logger

import javax.naming.InitialContext

import commons.settings.JNDIOrProperties
import dispatch.Defaults._
import dispatch._

import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration
import scala.util.parsing.json.JSONObject
import scala.util.{Try, Failure, Success}

object GoogleMobileNotification {
  lazy val topic: String =  JNDIOrProperties.get("gmc", "topic").getOrElse("global")
}

case class GoogleMobileNotification(name: String, key: String)
  extends Logger {

  val h = host("gcm-http.googleapis.com").secure
  val headers = Map(
    "Authorization" -> s"key=$key",
    "Content-Type" -> "application/json")

  def alert(message: String) = {

    val json = JSONObject(Map(
      "notification" -> JSONObject(Map(
        "sound" -> "default",
        "badge" -> "1",
        "title" -> name,
        "body" -> message,
        "icon" -> "ic_stat_ic_notification"
      )),
      "to" -> s"/topics/${ GoogleMobileNotification.topic }"
    ))
    val jsonString = json.toString()

    val req = h / "gcm" / "send" <:< headers << jsonString

    val http = new Http
    val push = http(req OK as.String)

    push onComplete {
      case Success(s) =>
        http.shutdown()
      case Failure(t) =>
        throw t
        http.shutdown()
    }

    //    Await.result(push, 30.seconds)
    //    http.shutdown()
  }
}
