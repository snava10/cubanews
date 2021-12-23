package com.snava.crawlandsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

public class Searcher {

  StandardAnalyzer analyzer = new StandardAnalyzer();

  public List<Document> search(String inField, String queryString, Directory index)
      throws IOException, ParseException {
    Query query = new QueryParser(inField, analyzer)
        .parse(queryString);

    IndexReader indexReader = DirectoryReader.open(index);
    IndexSearcher searcher = new IndexSearcher(indexReader);
    TopDocs topDocs = searcher.search(query, 10);
    List<Document> documents = new ArrayList<>();
    for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
      documents.add(searcher.doc(scoreDoc.doc));
    }

    return documents;
  }

  public List<Document> search(String queryString, Directory index)
      throws IOException, ParseException {
    return search("text", queryString, index);
  }

}
