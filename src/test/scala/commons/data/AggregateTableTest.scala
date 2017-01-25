package commons.data

import org.scalatest.FlatSpec
import net.liftweb.json._
import net.liftweb.json.Serialization.{read, write}

import scala.util.Random

/*
sbt "~rzepaw-commons/testOnly commons.data.AggregateTableTest"
 */

class AggregateTableTest
  extends FlatSpec {

  implicit val formats = Serialization.formats(NoTypeHints)

  val pt = new AggregateTable[Int, Int, Int](
    1 to 3,
    1 to 5) {
    override def _rowcol(r: Int, c: Int): Int = r+c
    override def _agg(cols: Iterable[Int]): Int = cols.sum
  }

  val ds = DescStats(List(1, 1, 1, 5))

  "Pivot table" should "be printable" in {
    println(pt)
  }

  "Descriptive statistics" should "be printable" in {
    println(ds)
  }
}
