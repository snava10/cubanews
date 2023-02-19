package com.snava.cubanews;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

  public List<SearcherResult> booleanSearch(List<String> fields, String queryString, Directory index, int top)
      throws IOException {
    List<Query> queries = fields.stream().map(field -> {
      try {
        return new QueryParser(field, analyzer).parse(queryString);
      } catch (ParseException e) {
        throw new RuntimeException(e);
      }
    }).toList();
    BooleanQuery.Builder builder = new Builder();
    queries.forEach(q -> builder.add(q, Occur.SHOULD));

    IndexReader indexReader = DirectoryReader.open(index);
    IndexSearcher searcher = new IndexSearcher(indexReader);
    TopDocs topDocs = searcher.search(builder.build(), top);
    List<SearcherResult> documents = new ArrayList<>();
    for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
      SearcherResult sr = new SearcherResult(scoreDoc.score, searcher.doc(scoreDoc.doc));
      documents.add(sr);
    }
    indexReader.close();
    return documents;
  }

  public List<SearcherResult>
  search(String queryString, Directory index, int top)
      throws IOException {
    return booleanSearch(Arrays.asList("title","url","text"), queryString, index, top);
  }

  record SearcherResult(float score, Document doc) {
  }


}
