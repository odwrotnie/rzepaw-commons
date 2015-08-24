package commons.search

//import scala.collection.mutable.ListBuffer
//
//import net.liftweb.common._

//import org.apache.lucene.document._
//import org.apache.lucene.store._
//import org.apache.lucene.analysis.standard._
//import org.apache.lucene.util._
//import org.apache.lucene.index._
//import org.apache.lucene.document._
//import org.apache.lucene.queryParser._
//import org.apache.lucene.search._
//import actors.Actor._
//import java.io.File
//import net.liftweb.mapper.{ByList, QueryParam}

/*

Slabo dziala,
wypierdala sie z Too many open files
i java NPE

trait Indexable {
  // TODO Change it to lazy vals
  def indexableId: String
  def indexableFieldsMap: Map[String, String]
  def indexableFields = {
    val lb = new ListBuffer[Field]
    lb += new Field(Indexable.INDEXABLE_ID, indexableId, Field.Store.YES, Field.Index.ANALYZED)
    for (tuple <- indexableFieldsMap) {
      tuple match {
        case (field, content) => {
          lb += new Field(field, content, Field.Store.YES, Field.Index.ANALYZED)
          lb += new Field(Indexable.ALL_SEARCH_FIELDS_NAME, content, Field.Store.YES, Field.Index.ANALYZED)
        }
      }
    }
    lb.toList
  }

  override def toString = "Indexable %s" format indexableId
}

object Indexable {

  val INDEX_INTERVAL = 6000000
  val INDEXABLE_ID = "id"
  val ALL_SEARCH_FIELDS_NAME = "all_fields"
  val URL_QUERY_PARAM = "query"

  val indexDirectory = new NIOFSDirectory(new File("lucene_index"))
  val indexAnalyzer = new StandardAnalyzer(Version.LUCENE_32)
  val indexWriter = new IndexWriter(indexDirectory, indexAnalyzer, true, IndexWriter.MaxFieldLength.UNLIMITED)
  val searcher = new IndexSearcher(indexDirectory, true)
}

trait IndexableMeta[I <: Indexable] extends Logger {

  def addToIndex(indexable: I) {
    info("Adding to index: %s" format indexable)
    val doc = new Document
    for (field <- indexable.indexableFields) {
      debug("Field [%s]: %.50s" format (field.name, field.stringValue))
      doc.add(field)
    }
    Indexable.indexWriter.addDocument(doc)
    Indexable.indexWriter.commit
  }

  def updateIndex(indexable: I) {
    debug("Update index: %s" format indexable)
    removeFromIndex(indexable)
    addToIndex(indexable)
  }

  def removeAllFromIndex {
    Indexable.indexWriter.deleteAll
    Indexable.indexWriter.commit
  }

  def removeFromIndex(id: String) {
    Indexable.indexWriter.deleteDocuments(new Term(Indexable.INDEXABLE_ID, id))
    Indexable.indexWriter.commit
  }

  def removeFromIndex(indexable: I) {
    debug("Remove from index: %s" format indexable)
    removeFromIndex(indexable.indexableId)
  }

  def getIndexed(id: String): Option[I]

  def searchIDs(query: String): List[String] = {
    debug("Searching for: [%s]" format query)
    if (query.isEmpty) {
      warn("Search string is empty")
      return Nil
    }
    val q = new QueryParser(Version.LUCENE_CURRENT, Indexable.ALL_SEARCH_FIELDS_NAME, Indexable.indexAnalyzer).parse(query)
    val collector = TopScoreDocCollector.create(100, true)
    Indexable.searcher.search(q, collector)
    val hits = collector.topDocs().scoreDocs
    debug("Found %s hits" format hits.length)
    val idsArray:Array[String] = hits.map(hit =>
      Indexable.searcher.doc(hit.doc).get(Indexable.INDEXABLE_ID))
    idsArray.toList
  }

  def search(query: String): List[I] = {
    searchIDs(query).flatMap(id => getIndexed(id) match {
      case Some(i) => Some(i)
      case _ => {
        error("There is no Indexable with %s: %s" format
          (Indexable.INDEXABLE_ID, id))
        removeFromIndex(id)
        None
      }
    })
  }

  def findAll: List[I]

  val cleanIndex = actor {
    loop {
      info("Cleaning index %s every %.1f minutes" format (getClass.getSimpleName, Indexable.INDEX_INTERVAL.toFloat / 60000))
      removeAllFromIndex
      for (i <- findAll) {
        addToIndex(i)
      }
      Indexable.indexWriter.optimize
      Thread.sleep(Indexable.INDEX_INTERVAL)
    }
  }
  cleanIndex.start
}

*/