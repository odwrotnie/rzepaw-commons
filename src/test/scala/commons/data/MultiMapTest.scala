package commons.data

import commons.text.MapPrinter
import org.scalatest.FlatSpec

/*
sbt "~rzepaw-commons/testOnly commons.data.MultiMapTest"
 */

class MultiMapTest
  extends FlatSpec {

  "MultiMap" should "work" in {
    val mm = MultiMap.empty[String, String]()
    mm.insert("person", "name")("John")
    mm.insert("person", "company", "name")("Subeli")
    mm.insert("person", "days", "1", "start-hour")("1")
    mm.insert("person", "days", "1", "end-hour")("1")
    mm.insert("person", "days", "1", "duration")("1")
    mm.insert("person", "days", "2", "start-hour")("2")
    mm.insert("person", "days", "2", "end-hour")("2")
    mm.insert("person", "days", "2", "duration")("2")
    println(s"MultiMap:\n$mm")
  }
}
