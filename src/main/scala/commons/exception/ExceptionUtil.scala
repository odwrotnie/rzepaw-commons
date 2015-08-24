//package commons.exception
//
//import xml.NodeSeq
//import net.liftweb.common.Full
//
//object ExceptionUtil {
//
//  def toNodeSeq(throwable: Throwable, filter: Option[String] = None): NodeSeq = {
//
//    var t = throwable
//    var ns: NodeSeq = <h1>Exception</h1> :: Nil
//
//    do {
//      ns = ns :+ <h2>{ t.toString }</h2> :+
//        <ul>
//          {
//          for (ste <- t.getStackTrace)
//          yield <li>{ ste }</li>
//          }
//        </ul>
//      t = t.getCause
//    } while (t != null)
//    ns
//  }
//}
