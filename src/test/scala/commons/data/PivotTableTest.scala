package commons.data

import org.scalatest.FlatSpec
import net.liftweb.json._
import net.liftweb.json.Serialization.{read, write}

import scala.util.Random

/*
sbt "~rzepaw-commons/testOnly commons.data.PivotTableTest"
 */

class PivotTableTest
  extends FlatSpec {

  implicit val formats = Serialization.formats(NoTypeHints)

  val pt = PivotTable[String, Int, Int](
    List("A", "B", "C"),
    1 to 5,
    (r: String, c: Int) => Random.nextInt(1000))

  val ds = DescStats(List(1, 1, 1, 5))

  "Pivot table" should "be printable" in {
    println(pt)
  }

  "Descriptive statistics" should "be printable" in {
    println(ds)
  }
}
