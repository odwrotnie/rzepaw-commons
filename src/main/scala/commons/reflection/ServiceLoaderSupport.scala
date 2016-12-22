package commons.reflection

import java.util.ServiceLoader
import scala.collection.JavaConverters._
import scala.reflect.ClassTag

/**
  * Place META-INF/services dir in resources
  * Then in this dir create interface name text files
  * Then in this file place implementation names
  */
trait ServiceLoaderSupport {

  def loadServices[T](clazz: Class[T]): Iterable[T] = {
    (ServiceLoader load clazz).asScala
  }

  def loadServices[T: ClassTag]: Iterable[T] = {
    def ctag = implicitly[ClassTag[T]]
    def clazz: Class[T] = ctag.runtimeClass.asInstanceOf[Class[T]]
    (ServiceLoader load clazz).asScala
  }
}
