package commons.net

case class Geolocation(ipAddress: String,
					   countryCode: String,
					   countryName: String,
					   regionName: String,
					   cityName: String,
					   zipCode: String,
					   latitude: Double,
					   longitude: Double,
					   timeZone: String)

object IP {

	val IPINFODB_KEY = "f5094f7fcd8df749d9958cd94842b6e84679f480db0ecd76d160f663a8089fd9"

	def toDec(a:Long, b:Long, c:Long, d:Long): Long = (a << 24) | (b << 16) | (c << 8) | (d)

	def toDec(ip: String): Long = {
		val ipArray = ip.split("\\.")
		toDec(ipArray(0).toLong,
			ipArray(1).toLong,
			ipArray(2).toLong,
			ipArray(3).toLong)
	}

//	def geolocation(ip: String) = {
//		val http = new Http
//		val req = :/("api.ipinfodb.com") / "v3" / "ip-city" <<? Map(
//			"key" -> IPINFODB_KEY,
//			"format" -> "xml",
//			"ip" -> ip)
//		val xml = http(req <> { _ \\ "Response"})
//		Geolocation(xml \ "ipAddress" text,
//			xml \ "countryCode" text,
//			xml \ "countryName" text,
//			xml \ "regionName" text,
//			xml \ "cityName" text,
//			xml \ "zipCode" text,
//			(xml \ "latitude").text.toDouble,
//			(xml \ "longitude").text.toDouble,
//			xml \ "timeZone" text)
//	}
}
