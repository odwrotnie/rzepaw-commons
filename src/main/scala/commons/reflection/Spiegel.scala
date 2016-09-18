package commons.reflection

import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._
import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.{ universe => ru }

object Spiegel {

  // implicit def getTypeTag[T: universe.TypeTag](obj: T) = universe.typeTag[T]

  val m = universe.runtimeMirror(getClass.getClassLoader)

  def constructor[T](implicit tt: TypeTag[T]): MethodMirror = {
    val classPerson = universe.typeOf[T].typeSymbol.asClass
    val cm = m.reflectClass(classPerson)
    val ctor = universe.typeOf[T].decl(universe.termNames.CONSTRUCTOR).asMethod
    cm.reflectConstructor(ctor)
  }

  def instance[T](constructorArgs: Any*)(implicit tt: TypeTag[T]): T =
    constructor[T].apply(constructorArgs).asInstanceOf[T]

  def companion[T](implicit tt: TypeTag[T]): Any = {
    val module = tt.tpe.typeSymbol.asClass.companion.asModule
    m.reflectModule(module).instance
  }

  def companion(a: Any): Any = companion(a.getClass)
  def companion(c: Class[_]): Any = {
    val module = m.classSymbol(c).companion.asModule
    m.reflectModule(module).instance
  }

  // Shit
  def caseObjects[Root: TypeTag]: Set[Symbol] = {
    val symbol = typeOf[Root].typeSymbol
    val internal = symbol.asInstanceOf[scala.reflect.internal.Symbols#Symbol]
    val x = internal.sealedDescendants.filter(_.isValue)
    println(s"\n\n\n X: $x\n\n")
    internal.sealedDescendants.map(_.asInstanceOf[Symbol])
  }

//  def instance[T](clazz: Class[T], constructorArgs: Object*): T = {
//    val constructor = clazz.getConstructor(constructorArgs.map(_.getClass):_*)
//    constructor.newInstance(constructorArgs:_*).asInstanceOf[T]
//  }
}
