package com.snava.cubanews;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.BoostQuery;
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

  public List<SearcherResult> booleanSearch(List<String> fields, String queryString,
      Directory index, int top)
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
    TopDocs topDocs = searcher.search(
        boostLast24hourDocs(builder.build(), "lastUpdatedNumericStored", 2f), top);
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
    return booleanSearch(Arrays.asList("title", "url", "text"), queryString, index, top);
  }

  public record SearcherResult(float score, Document doc) {
  }

  public Query boostLast24hourDocs(Query mainQuery, String updatedField, float boostFactor) {
    // Create a filter for recent documents based on the updatedField
    long currentTime = Instant.now().toEpochMilli();
    long oneDayAgo =
        currentTime - (24 * 60 * 60 * 1000); // Boost documents updated within the last 24h
    Query recentDocumentsQuery = LongPoint.newRangeQuery(updatedField, oneDayAgo, currentTime);

    // Boost the main query using the recentDocumentsQuery
    BoostQuery boostQuery = new BoostQuery(mainQuery, boostFactor);

    // Combine the main query with the recent documents filter using a BooleanQuery
    BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
    booleanQueryBuilder.add(boostQuery, Occur.MUST);
    booleanQueryBuilder.add(recentDocumentsQuery, Occur.SHOULD);

    return booleanQueryBuilder.build();
  }
}
