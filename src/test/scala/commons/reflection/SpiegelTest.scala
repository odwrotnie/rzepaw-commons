package commons.reflection

import org.clapper.classutil.ClassFinder
import org.scalatest.FlatSpec

/*
sbt "~rzepawCommons/testOnly commons.reflection.SpiegelTest"
 */

abstract class Drink {
  override def toString = getClass.getSimpleName
}

abstract class SoftDrink extends Drink
sealed class Cola extends SoftDrink
object Cola
sealed class TonicWater extends SoftDrink
object TonicWater

abstract class Juice extends Drink
class AppleJuice extends Juice
class OrangeJuice extends Juice

class SpiegelTest
  extends FlatSpec {

  val cola = new Cola
  val tonic = new TonicWater
  val appleJuice = new AppleJuice
  val orangeJuice = new OrangeJuice

  "Instance" should "return an instance" in {
    val cola = Spiegel.instance(classOf[Cola])
    info(s"Cola: $cola")
    assert(cola.isInstanceOf[Cola])
    assert(!cola.isInstanceOf[TonicWater])
  }

  "Companion" should "return proper class" in {
    assert(Cola == Spiegel.companion[Cola])
    assert(Cola == Spiegel.companion(cola.getClass))
    assert(Cola == Spiegel.companion(cola))
  }
}
