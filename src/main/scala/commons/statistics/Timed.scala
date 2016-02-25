package commons.statistics

import commons.numbers.HumanReadable
import commons.logger.Logger

case class Timed(name: String, repeats: Int = 1)
  extends Logger {
  var s = System.nanoTime
  var e = System.nanoTime

  def time = (e - s) / repeats
  def timeString = HumanReadable.nanoseconds(time)

  def start { s = System.nanoTime }
  def end { e = System.nanoTime }

  def measure[T](thunk: => T) = {
    start
    val ret: T = thunk
    (2 to repeats) foreach { _ => thunk }
    end
    ret
  }

  def log[T](thunk: => T) = {
    val ret = measure(thunk)
    debug(this.toString)
    ret
  }

  override def toString = s"[$name] Executed in $timeString"
}
