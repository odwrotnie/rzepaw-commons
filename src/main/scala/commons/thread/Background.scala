package commons.thread

import scala.actors._
import Actor._

object Background {

  val MILLISECONDS_IN_SECOND = 1000

  def repeat(secondsDelay: Int)(x: => Unit) {
    val millisecondsDelay = secondsDelay * MILLISECONDS_IN_SECOND
    actor {
      loop {
        x
        Thread.sleep(millisecondsDelay)
      }
    }
  }

  def apply(x: => Unit) {
    actor {
      x
    }
  }
}
