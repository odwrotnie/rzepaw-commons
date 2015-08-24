package commons.numbers

import scala.collection.immutable._

object HumanReadable {

  def pick(value: Long, m: List[(Long, String)]) = {
    m.map {
      case (l, name) => (value.toFloat / l) -> name
    }.reverse.filter(kv => kv._1 > 1).headOption.map {
      case (v, name) => f"$v%.1f $name"
    }.getOrElse("0")
  }

  def nanoseconds(nanoseconds: Long) = pick(
    nanoseconds,
    TreeMap(
      1l -> "nanoseconds",
      1000000l -> "milliseconds",
      1000000000l -> "seconds",
      60000000000l -> "minutes").toList
  )

  def milliseconds(milliseconds: Long) = pick(
    milliseconds,
    TreeMap(
      1l -> "milliseconds",
      1000l -> "seconds",
      60000l -> "minutes",
      3600000l -> "hours",
      86400000l -> "days",
      604800000l -> "week").toList
  )

  def seconds(seconds: Long) = pick(
    seconds,
    TreeMap(
      1l -> "seconds",
      60l -> "minutes",
      3600l -> "hours",
      86400l -> "days",
      604800l -> "week").toList
  )

  def bytes(bytes: Long) = pick(
    bytes,
    TreeMap(0 -> "B", 1 -> "KB", 2 -> "MB", 3 -> "GB", 4 -> "TB").map {
      case (power, name) => math.pow(2, power * 10).toLong -> name
    }.toList
  )
}
