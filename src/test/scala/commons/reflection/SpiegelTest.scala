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

  "Companion" should "return proper class" in {
    assert(Cola == Spiegel.companion[Cola])
    assert(Cola == Spiegel.companion(cola.getClass))
    assert(Cola == Spiegel.companion(cola))
  }

//  "asdf" should "qwer" in {
//    val cola = new Cola
//    println(cola)
//    val finder = ClassFinder()
//    val classes = finder.getClasses
//    println("Classpath: " + System.getProperty("java.class.path"))
//    classes foreach { c =>
//      println(s" > $c")
//    }
//    //val drinks = ClassFinder.concreteSubclasses(classOf[SoftDrink], classes)
//    val drinks = ClassFinder.concreteSubclasses("commons.reflection", classes)
//    println("Drinks: " + drinks)
//  }

  object Test {
    sealed class SuperParent
    sealed class Parent extends SuperParent
    object A extends Parent
    object B extends Parent
    object C extends Parent
  }

  "Class descendants" should "return proper list" in {
    println(Spiegel.caseObjects[Test.SuperParent])
  }
}
