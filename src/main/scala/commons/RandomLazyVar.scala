//package commons
//
//import util.Random
//import scala.actors.Actor
//import scala.actors.Actor._
//
//abstract class RandomLazyVar[T] {
//
//  val treshold = 0.3
//
//  def refresh: T
//
//  var o: Option[T] = None
//  def get: T = {
//    o match {
//      case Some(v) => {
//        if (Random.nextDouble <= treshold) // If random
//          actor { // Set new value in background
//            o = Some(refresh)
//          }
//        o.get // Return old value
//      }
//      case None => {
//        o = Some(refresh)
//        o.get
//      }
//    }
//  }
//}
