package commons.statistics

import commons.numbers.HumanReadable
import commons.logger.Logger

case class Timed(name: String)
  extends Logger {
  var s = System.nanoTime
  var e = System.nanoTime
  def time = e - s
  def start { s = System.nanoTime }
  def end { e = System.nanoTime }
  def measure[T](thunk: => T) = {
    start
    val ret = thunk
    end
    ret
  }
  def log[T](thunk: => T) = {
    val ret = measure(thunk)
    debug(this.toString)
    ret
  }
  override def toString = "[%s] Executed in %s"
    .format(name, HumanReadable.nanoseconds(time))
}
