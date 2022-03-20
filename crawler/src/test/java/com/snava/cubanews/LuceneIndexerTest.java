package com.snava.cubanews;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHits;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LuceneIndexerTest {

  Directory memoryIndex;
  Analyzer analyzer;
  IndexWriterConfig indexWriterConfig;
  Indexer indexer;
  IndexReader indexReader;
  IndexSearcher searcher;

  @BeforeEach
  void setUp() throws IOException {
    memoryIndex = new ByteBuffersDirectory();
    analyzer = new StandardAnalyzer();
    indexWriterConfig = new IndexWriterConfig(analyzer);
    indexer = new LuceneIndexer(memoryIndex, analyzer);
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void close() {
  }

  @Test
  void index() throws Exception {
    IndexDocument iDocument1 = ImmutableIndexDocument.builder()
        .url("http://url1.com")
        .title("doc1")
        .text("doc1 content")
        .build();
    indexer.index(iDocument1);
    indexReader = DirectoryReader.open(memoryIndex);
    searcher = new IndexSearcher(indexReader);
    int count = searcher.count(new TermQuery(new Term("_id", "http://url1.com")));
    assertEquals(1, count);
  }

  @Test
  void index_shouldNotDuplicateDocuments() throws IOException {
    IndexDocument iDocument1 = ImmutableIndexDocument.builder()
        .url("http://url1.com")
        .title("doc1")
        .text("doc1 content")
        .build();
    indexer.index(iDocument1);
    indexer.index(iDocument1);
    indexReader = DirectoryReader.open(memoryIndex);
    searcher = new IndexSearcher(indexReader);
    int count = searcher.count(new TermQuery(new Term("_id", "http://url1.com")));
    assertEquals(1, count);
  }

  @Test
  void index_ShouldStoreLastUpdated() throws Exception {
    IndexDocument iDocument1 = ImmutableIndexDocument.builder()
        .url("http://url1.com")
        .title("doc1")
        .text("doc1 content")
        .build();
    indexer.index(iDocument1);
    indexReader = DirectoryReader.open(memoryIndex);
    searcher = new IndexSearcher(indexReader);
    TopDocs topDocs = searcher.search(new TermQuery(new Term("_id", "http://url1.com")), 1);
    String lastUpdated = searcher.doc(topDocs.scoreDocs[0].doc).get("lastUpdated");
    assertNotNull(lastUpdated);
    assertTrue(Long.parseLong(lastUpdated) > 0);
  }

  @Test
  void delete() throws Exception {
    List<String> urls = new ArrayList<>();
    for (int i = 0; i < 50; i++) {
      urls.add("http://url" + i);
      IndexDocument iDocument1 = ImmutableIndexDocument.builder()
          .url("http://url" + i)
          .title("doc1")
          .text("doc1 content")
          .build();
      indexer.index(iDocument1);
    }
    TermQuery t = new TermQuery(new Term("title", "doc1"));
    indexReader = DirectoryReader.open(memoryIndex);
    searcher = new IndexSearcher(indexReader);
    TopDocs topDocs = searcher.search(t, 100);
    assertThat(topDocs.totalHits.value).isEqualTo(50L);
    indexReader.close();

    assertThat(((AbstractIndexer)indexer).hasDeletions()).isFalse();
    indexer.delete(urls);

    indexReader = DirectoryReader.open(memoryIndex);
    searcher = new IndexSearcher(indexReader);
    topDocs = searcher.search(t, 100);
    assertThat(topDocs.totalHits.value).isEqualTo(0L);
    indexReader.close();
  }

}