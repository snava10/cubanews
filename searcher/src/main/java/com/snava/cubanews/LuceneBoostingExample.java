package com.snava.cubanews;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queries.function.FunctionScoreQuery;
import org.apache.lucene.search.DoubleValuesSource;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneBoostingExample {

  public static void main(String[] args) {
    try {
      // Create an in-memory index
      final String indexPath = args[0];
      Directory directory = FSDirectory.open(Paths.get(indexPath));
      IndexWriterConfig config = new IndexWriterConfig();
      IndexWriter writer = new IndexWriter(directory, config);

      // Add sample documents to the index
      addDocument(writer, "Document 1", "This is the content of Document 1.", Instant.now().minusMillis(5000));
      addDocument(writer, "Document 2", "This is the content of Document 2.", Instant.now().minusMillis(2000));
      addDocument(writer, "Document 3", "This is the content of Document 3.", Instant.now().minusMillis(1000));
      addDocument(writer, "Document 4", "This is the content of Document 4.", Instant.now().minusMillis(3000));

      writer.close();

      // Search the index with boosting based on lastUpdated field
      IndexReader reader = DirectoryReader.open(directory);
      IndexSearcher searcher = new IndexSearcher(reader);

      // Create the original query
      Query originalQuery = new MatchAllDocsQuery();

      // Create a function query to boost based on the proximity to the current date
      DoubleValuesSource valuesSource = DoubleValuesSource.fromQuery(
          LongPoint.newRangeQuery("lastUpdated", Instant.now().toEpochMilli()-1, Instant.now().toEpochMilli())
      );

      FunctionScoreQuery functionScoreQuery = FunctionScoreQuery.boostByValue(originalQuery, valuesSource);

      // Perform the search and display the results
      TopDocs topDocs = searcher.search(functionScoreQuery, 10);
      ScoreDoc[] scoreDocs = topDocs.scoreDocs;

      System.out.println("Search results:");
      for (ScoreDoc scoreDoc : scoreDocs) {
        int docId = scoreDoc.doc;
        Document document = searcher.doc(docId);
        System.out.println("Document: " + document.get("title") + " (Score: " + scoreDoc.score + ")");
      }

      reader.close();
      directory.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Method to add a document to the index
  private static void addDocument(IndexWriter writer, String title, String content, Instant lastUpdated) throws IOException {
    Document document = new Document();
    document.add(new TextField("title", title, Field.Store.YES));
    document.add(new TextField("content", content, Field.Store.YES));
    document.add(new LongPoint("lastUpdated", lastUpdated.toEpochMilli()));
    writer.addDocument(document);
  }
}
