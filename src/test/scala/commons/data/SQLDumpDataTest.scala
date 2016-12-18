package commons.data

import com.typesafe.scalalogging.LazyLogging
import net.sf.jsqlparser.expression.operators.arithmetic._
import net.sf.jsqlparser.expression.operators.conditional.{AndExpression, OrExpression}
import net.sf.jsqlparser.expression._
import net.sf.jsqlparser.expression.operators.relational._
import org.scalatest.FlatSpec
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Column
import net.sf.jsqlparser.statement.alter.Alter
import net.sf.jsqlparser.statement.create.index.CreateIndex
import net.sf.jsqlparser.statement.create.table.CreateTable
import net.sf.jsqlparser.statement.create.view.{AlterView, CreateView}
import net.sf.jsqlparser.statement.delete.Delete
import net.sf.jsqlparser.statement.drop.Drop
import net.sf.jsqlparser.statement.{SetStatement, StatementVisitor, Statements}
import net.sf.jsqlparser.statement.execute.Execute
import net.sf.jsqlparser.statement.insert.Insert
import net.sf.jsqlparser.statement.merge.Merge
import net.sf.jsqlparser.statement.replace.Replace
import net.sf.jsqlparser.statement.select.{Select, SubSelect}
import net.sf.jsqlparser.statement.truncate.Truncate
import net.sf.jsqlparser.statement.update.Update

import scala.collection.JavaConversions._
import scala.util.Random

/*
sbt "~rzepaw-commons/testOnly commons.data.SQLDumpDataTest"
 */

class SQLDumpDataTest
  extends FlatSpec
  with LazyLogging {

  "SQLDumpData" should "read inserts" in {
    val stmt = CCJSqlParserUtil.parse("SELECT * FROM tab1")
    println(stmt)
    val stmts = CCJSqlParserUtil.parseStatements("SELECT * FROM tab1; SELECT * FROM tab2; INSERT INTO tab1 (c1, c2, c3) VALUES (value1,value2,value3);");
    stmts.getStatements foreach {

      case insert: Insert =>
        insert.accept(X)
        logger.info(s"Insert: $insert")
        logger.info(s" > table: ${ insert.getTable }")
        logger.info(s" > columns: ${ insert.getColumns }")
        logger.info(s" > items: ${ insert.getItemsList }")
      case s => logger.debug(s"Other statement: $s")
    }
  }
}

object X
  extends StatementVisitor {

  override def visit(truncate: Truncate): Unit = ???

  override def visit(drop: Drop): Unit = ???

  override def visit(replace: Replace): Unit = ???

  override def visit(createView: CreateView): Unit = ???

  override def visit(createTable: CreateTable): Unit = ???

  override def visit(createIndex: CreateIndex): Unit = ???

  override def visit(stmts: Statements): Unit = ???

  override def visit(alter: Alter): Unit = ???

  override def visit(alterView: AlterView): Unit = ???

  override def visit(merge: Merge): Unit = ???

  override def visit(set: SetStatement): Unit = ???

  override def visit(execute: Execute): Unit = ???

  override def visit(select: Select): Unit = ???

  override def visit(delete: Delete): Unit = ???

  override def visit(update: Update): Unit = ???

  override def visit(insert: Insert): Unit = insert.accept(X)
}
