package commons.reflection

import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._

object Spiegel {

  // implicit def getTypeTag[T: universe.TypeTag](obj: T) = universe.typeTag[T]

  def constructor[T](implicit tt: TypeTag[T]) = {
    val m = universe.runtimeMirror(getClass.getClassLoader)
    val classPerson = universe.typeOf[T].typeSymbol.asClass
    val cm = m.reflectClass(classPerson)
    val ctor = universe.typeOf[T].decl(universe.termNames.CONSTRUCTOR).asMethod
    cm.reflectConstructor(ctor)
  }
}
