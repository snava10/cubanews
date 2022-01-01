package com.snava.crawlandsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

public class Searcher {

  StandardAnalyzer analyzer = new StandardAnalyzer();

  public List<Document> search(String inField, String queryString, Directory index, int top)
      throws IOException, ParseException {
    Query query = new QueryParser(inField, analyzer)
        .parse(queryString);

    IndexReader indexReader = DirectoryReader.open(index);
    IndexSearcher searcher = new IndexSearcher(indexReader);
    TopDocs topDocs = searcher.search(query, top);
    List<Document> documents = new ArrayList<>();
    for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
      documents.add(searcher.doc(scoreDoc.doc));
    }
    return documents;
  }

  public List<Document> booleanSearch(List<String> fields, String queryString, Directory index, int top)
      throws IOException {
    List<Query> queries = fields.stream().map(field -> {
      try {
        return new QueryParser(field, analyzer).parse(queryString);
      } catch (ParseException e) {
        throw new RuntimeException(e);
      }
    }).collect(Collectors.toList());
    BooleanQuery.Builder builder = new Builder();
    queries.forEach(q -> builder.add(q, Occur.SHOULD));

    IndexReader indexReader = DirectoryReader.open(index);
    IndexSearcher searcher = new IndexSearcher(indexReader);
    TopDocs topDocs = searcher.search(builder.build(), top);
    List<Document> documents = new ArrayList<>();
    for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
      documents.add(searcher.doc(scoreDoc.doc));
    }
    indexReader.close();
    return documents;
  }

  public List<Document> search(String queryString, Directory index, int top)
      throws IOException {
    return booleanSearch(Arrays.asList("title","url","text"), queryString, index, top);
  }

}
