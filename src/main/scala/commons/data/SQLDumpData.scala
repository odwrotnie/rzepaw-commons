package commons.data

import net.sf.jsqlparser.expression.operators.arithmetic._
import net.sf.jsqlparser.expression.operators.conditional.{AndExpression, OrExpression}
import net.sf.jsqlparser.expression._
import net.sf.jsqlparser.expression.operators.relational._
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Column
import net.sf.jsqlparser.statement.alter.Alter
import net.sf.jsqlparser.statement.{SetStatement, StatementVisitor, Statements}
import net.sf.jsqlparser.statement.create.index.CreateIndex
import net.sf.jsqlparser.statement.create.table.CreateTable
import net.sf.jsqlparser.statement.create.view.{AlterView, CreateView}
import net.sf.jsqlparser.statement.delete.Delete
import net.sf.jsqlparser.statement.drop.Drop
import net.sf.jsqlparser.statement.execute.Execute
import net.sf.jsqlparser.statement.insert.Insert
import net.sf.jsqlparser.statement.merge.Merge
import net.sf.jsqlparser.statement.replace.Replace
import net.sf.jsqlparser.statement.select.{Select, SubSelect}
import net.sf.jsqlparser.statement.truncate.Truncate
import net.sf.jsqlparser.statement.update.Update

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Library: https://github.com/JSQLParser/JSqlParser
  * API: http://jsqlparser.sourceforge.net/docs/
  * Example: http://jsqlparser.sourceforge.net/example.php
  * Old website: http://jsqlparser.sourceforge.net/home.php
  *
  * !!! JEST JAKIS PROBLEM Z IF IF - https://github.com/JSQLParser/JSqlParser/issues/100
  * NAJPROSCIEJ USUNAC TE LINIE
  *
  * !!! PROBLEMY PRZY REPLACE
  * NAJLEPIEJ USUNAC TE LINIE
  */
case class SQLDumpData(sql: String) {

  lazy val tableInsertsMap: mutable.HashMap[String, ListBuffer[List[Any]]] = {
    val stmts = CCJSqlParserUtil.parseStatements(sql)
    val sv = new SV
    stmts.accept(sv)
    sv.tableInserts
  }

  def inserts(table: String): List[List[Any]] = tableInsertsMap.get(table).get.toList

  def map[T](table: String, m: List[Any] => T): List[T] = inserts(table).map(m)

  override def toString: String = tableInsertsMap.keySet map { key =>
    s"$key: ${ tableInsertsMap.get(key).toList.flatten.size }"
  } mkString(", ")

  class SV
    extends StatementVisitor {

    var tableInserts = new mutable.HashMap[String, ListBuffer[List[Any]]]()

    override def visit(truncate: Truncate): Unit = {}

    override def visit(drop: Drop): Unit = {}

    override def visit(replace: Replace): Unit = {}

    override def visit(createView: CreateView): Unit = {}

    override def visit(createTable: CreateTable): Unit = {}

    override def visit(createIndex: CreateIndex): Unit = {}

    override def visit(stmts: Statements): Unit = stmts.getStatements foreach { stmt =>
      stmt.accept(this)
    }

    override def visit(alter: Alter): Unit = {}

    override def visit(alterView: AlterView): Unit = {}

    override def visit(merge: Merge): Unit = {}

    override def visit(set: SetStatement): Unit = {}

    override def visit(execute: Execute): Unit = {}

    override def visit(select: Select): Unit = {}

    override def visit(delete: Delete): Unit = {}

    override def visit(update: Update): Unit = {}

    override def visit(insert: Insert): Unit = {
      val ilv = new ILV
      val tableName = insert.getTable.getName.replace("`", "")
      insert.getItemsList.accept(ilv)
      tableInserts.get(tableName) match {
        case Some(lb) =>
          lb += ilv.result.toList
        case None =>
          tableInserts += (tableName -> ilv.result)
      }
    }
  }

  class ILV
    extends ItemsListVisitor {

    val result = collection.mutable.ListBuffer[List[Any]]()

    override def visit(subSelect: SubSelect): Unit = ???

    override def visit(expressionList: ExpressionList): Unit = {
      val list = expressionList.getExpressions.toList map { expression =>
        val ev = new EV
        expression.accept(ev)
        ev.result
      }
      result += list
    }

    override def visit(multiExprList: MultiExpressionList): Unit =
      multiExprList.getExprList foreach { expressionList =>
        expressionList.accept(this)
      }
  }

  class EV
    extends ExpressionVisitor {

    var result: Any = _

    override def visit(notEqualsTo: NotEqualsTo): Unit = ???

    override def visit(minorThanEquals: MinorThanEquals): Unit = ???

    override def visit(minorThan: MinorThan): Unit = ???

    override def visit(likeExpression: LikeExpression): Unit = ???

    override def visit(stringValue: StringValue): Unit = result = stringValue.getValue

    override def visit(parenthesis: Parenthesis): Unit = ???

    override def visit(timestampValue: TimestampValue): Unit = ???

    override def visit(timeValue: TimeValue): Unit = ???

    override def visit(dateValue: DateValue): Unit = ???

    override def visit(hexValue: HexValue): Unit = ???

    override def visit(iexpr: IntervalExpression): Unit = ???

    override def visit(oexpr: OracleHierarchicalExpression): Unit = ???

    override def visit(rexpr: RegExpMatchOperator): Unit = ???

    override def visit(addition: Addition): Unit = ???

    override def visit(division: Division): Unit = ???

    override def visit(multiplication: Multiplication): Unit = ???

    override def visit(subtraction: Subtraction): Unit = ???

    override def visit(andExpression: AndExpression): Unit = ???

    override def visit(orExpression: OrExpression): Unit = ???

    override def visit(between: Between): Unit = ???

    override def visit(equalsTo: EqualsTo): Unit = ???

    override def visit(greaterThan: GreaterThan): Unit = ???

    override def visit(bitwiseAnd: BitwiseAnd): Unit = ???

    override def visit(matches: Matches): Unit = ???

    override def visit(concat: Concat): Unit = ???

    override def visit(anyComparisonExpression: AnyComparisonExpression): Unit = ???

    override def visit(allComparisonExpression: AllComparisonExpression): Unit = ???

    override def visit(existsExpression: ExistsExpression): Unit = ???

    override def visit(whenClause: WhenClause): Unit = ???

    override def visit(caseExpression: CaseExpression): Unit = ???

    override def visit(subSelect: SubSelect): Unit = ???

    override def visit(tableColumn: Column): Unit = result = tableColumn.getColumnName

    override def visit(hint: OracleHint): Unit = ???

    override def visit(timeKeyExpression: TimeKeyExpression): Unit = ???

    override def visit(literal: DateTimeLiteralExpression): Unit = ???

    override def visit(eexpr: ExtractExpression): Unit = ???

    override def visit(wgexpr: WithinGroupExpression): Unit = ???

    override def visit(aexpr: AnalyticExpression): Unit = ???

    override def visit(modulo: Modulo): Unit = ???

    override def visit(cast: CastExpression): Unit = ???

    override def visit(bitwiseXor: BitwiseXor): Unit = ???

    override def visit(bitwiseOr: BitwiseOr): Unit = ???

    override def visit(signedExpression: SignedExpression): Unit = ???

    override def visit(function: Function): Unit = ???

    override def visit(nullValue: NullValue): Unit = result = None

    override def visit(rowConstructor: RowConstructor): Unit = ???

    override def visit(groupConcat: MySQLGroupConcat): Unit = ???

    override def visit(aexpr: KeepExpression): Unit = ???

    override def visit(bind: NumericBind): Unit = ???

    override def visit(`var`: UserVariable): Unit = ???

    override def visit(regExpMySQLOperator: RegExpMySQLOperator): Unit = ???

    override def visit(jsonExpr: JsonExpression): Unit = ???

    override def visit(isNullExpression: IsNullExpression): Unit = ???

    override def visit(inExpression: InExpression): Unit = ???

    override def visit(greaterThanEquals: GreaterThanEquals): Unit = ???

    override def visit(jdbcParameter: JdbcParameter): Unit = ???

    override def visit(jdbcNamedParameter: JdbcNamedParameter): Unit = ???

    override def visit(doubleValue: DoubleValue): Unit = ???

    override def visit(longValue: LongValue): Unit = result = longValue.getValue
  }
}
