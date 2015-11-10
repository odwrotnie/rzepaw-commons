//package commons.reflection
//
//import scala.reflect.runtime.universe
//
//object Spiegel {
//
//  implicit def getTypeTag[T: universe.TypeTag](obj: T) = universe.typeTag[T]
//
//  def constructor[T] = {
//    val m = universe.runtimeMirror(getClass.getClassLoader)
//    val classPerson = universe.typeOf[T].typeSymbol.asClass
//    val cm = m.reflectClass(classPerson)
//    val ctor = universe.typeOf[T].decl(universe.termNames.CONSTRUCTOR).asMethod
//    cm.reflectConstructor(ctor)
//  }
//}
