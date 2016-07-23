package commons.files

import java.io.{ObjectInputStream, FileInputStream, ObjectOutputStream, FileOutputStream}

import scala.util.Try

case class ObjectSerializer[T](file: String) {

  def serialize(something: T): Unit = {
    val fos = new FileOutputStream(file)
    val oos = new ObjectOutputStream(fos)
    oos.writeObject(something)
    oos.close()
  }

  def deserialize: Option[T] = Try {
    val fis = new FileInputStream(file)
    val ois = new ObjectInputStream(fis)
    val something = ois.readObject().asInstanceOf[T]
    ois.close()
    something
  } toOption
}
