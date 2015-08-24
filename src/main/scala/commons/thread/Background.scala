package commons.thread

import scala.actors._
import Actor._

object Background {

  def repeat(delay: Int)(x: => Unit) {
    actor {
      loop {
        x
        Thread.sleep(delay)
      }
    }
  }

  def apply(x: => Unit) {
    actor {
      x
    }
  }
}
