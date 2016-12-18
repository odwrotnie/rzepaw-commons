package commons.data

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.FlatSpec

/*
sbt "~rzepaw-commons/testOnly commons.data.SQLDumpDataTest"
 */

class SQLDumpDataTest
  extends FlatSpec
    with LazyLogging {

  "SQLDumpData" should "read inserts" in {

    val sql =
      """SELECT * FROM tab1;
        |SELECT * FROM tab2;
        |INSERT INTO T1 (c1, c2, c3) VALUES ('str',1,NULL), ('str',2,NULL);
        |INSERT INTO T2 VALUES (1, 2, 3, 4, 5);""".stripMargin

    val r = SQLDumpData(sql)

    logger.info(s"DUMPED: $r")

//    val stmt = CCJSqlParserUtil.parse("SELECT * FROM tab1")
//    println(stmt)
//    val stmts = CCJSqlParserUtil.parseStatements("""SELECT * FROM tab1; SELECT * FROM tab2; INSERT INTO tab1 (c1, c2, c3) VALUES ('str',1,NULL),('str',2,NULL);""");
//    stmts.getStatements foreach {
//
//      case insert: Insert =>
//        logger.info(s"Insert: $insert")
//        logger.info(s" > table: ${ insert.getTable }")
//        logger.info(s" > columns: ${ insert.getColumns }")
//        logger.info(s" > items: ${ insert.getItemsList }")
//        val sv = new SV
//        insert.accept(sv)
//        logger.info(s" > SV: ${ sv.result }")
//      case s => logger.debug(s"Other statement: $s")
//    }
  }
}
