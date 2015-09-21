package commons.logger

import dispatch.Defaults._
import dispatch._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.parsing.json.JSONObject

case class GoogleMobileNotification(name: String, key: String) {

  def alert(message: String) = {
    val h = host("gcm-http.googleapis.com").secure

    val headers = Map(
      "Authorization" -> s"key=$key",
      "Content-Type" -> "application/json")

    val json = JSONObject(Map(
      "notification" -> JSONObject(Map(
        "sound" -> "default",
        "badge" -> "1",
        "title" -> name,
        "body" -> message,
        "icon" -> "ic_stat_ic_notification"
      )),
      "to" -> "/topics/global"
    ))


    val req = h / "gcm" / "send" <:< headers << json.toString()

    val http = new Http
    val push = http(req OK as.String)

    val dm = Await.result(push, 15.seconds)
    http.shutdown()
    dm
  }
}
