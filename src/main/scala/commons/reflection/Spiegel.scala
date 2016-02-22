package commons.reflection

import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._

object Spiegel {

  // implicit def getTypeTag[T: universe.TypeTag](obj: T) = universe.typeTag[T]

  val m = universe.runtimeMirror(getClass.getClassLoader)

  def constructor[T](implicit tt: TypeTag[T]) = {
    val classPerson = universe.typeOf[T].typeSymbol.asClass
    val cm = m.reflectClass(classPerson)
    val ctor = universe.typeOf[T].decl(universe.termNames.CONSTRUCTOR).asMethod
    cm.reflectConstructor(ctor)
  }

  def companion[T](implicit tt: TypeTag[T]): Any = {
    val m = runtimeMirror(getClass.getClassLoader)
    val module = tt.tpe.typeSymbol.asClass.companion.asModule
    m.reflectModule(module).instance
  }
}
