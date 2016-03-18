package commons.data

import org.scalatest.FlatSpec

/*
sbt "~rzepawCommons/testOnly commons.data.PivotTableTest"
 */

class PivotTableTest
  extends FlatSpec {

  val pt = PivotTable[String, Int, String, Int](
    List("A", "B", "C"),
    1 to 5,
    (r: String, c: Int) => s"$r:$c")

  "Pivot table" should "be printable" in {
    println(pt)
  }
}
