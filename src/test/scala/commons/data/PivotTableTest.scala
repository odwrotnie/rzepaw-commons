package commons.data

import org.scalatest.FlatSpec
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import net.liftweb.json.Serialization.{read, write}

import scala.util.Random

/*
sbt "~rzepawCommons/testOnly commons.data.PivotTableTest"
 */

class PivotTableTest
  extends FlatSpec {

  implicit val formats = Serialization.formats(NoTypeHints)

  val pt = PivotTable[String, Int, Int](
    List("A", "B", "C"),
    1 to 5,
    (r: String, c: Int) => Random.nextInt(1000))

  "Pivot table" should "be printable" in {
    println(pt)
  }
}
