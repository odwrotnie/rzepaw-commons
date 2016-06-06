package slicky.helpers

import java.net.URI

import commons.date.DateUtil
import commons.logger.Logger
import slicky.entity.EntityMeta
import slicky.Slicky._
import driver.api._
import java.io._

case class Evolutions(file: String, metas: EntityMeta[_]*)
  extends Logger {

  val HEADER = s"# --- Database schema ${ DateUtil.formatTime(DateUtil.now) }"
  val UPS = "# --- !Ups"
  val DOWNS = "# --- !Downs"

  private def ups(schemas: driver.SchemaDescription*): List[String] = schemas.flatMap(_.createStatements).toList.map(_ + ";")
  private def downs(schemas: driver.SchemaDescription*): List[String] = schemas.flatMap(_.dropStatements).toList.map(_ + ";")

  private def ups(meta: EntityMeta[_]): List[String] = ups(meta.table.schema)
  private def downs(meta: EntityMeta[_]): List[String] = downs(meta.table.schema)

  lazy val lines: List[String] = HEADER ::
    UPS ::
    metas.flatMap(ups).toList :::
    DOWNS ::
    metas.flatMap(downs).toList

  def generate(overwrite: Boolean = false): Unit = {
    val outputFile = new File(file)
    if (!outputFile.exists() || overwrite) {
      val writer = new PrintWriter(outputFile)
      lines.foreach(writer.println)
      writer.close()
    }
  }

  override def toString: String = {
    lines.mkString("\n")
  }
}
