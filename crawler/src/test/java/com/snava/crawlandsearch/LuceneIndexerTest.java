package com.snava.crawlandsearch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
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
    IndexDocument iDocument1 = new IndexDocument("http://url1.com", "doc1", "doc1 content");
    indexer.index(iDocument1);
    indexReader = DirectoryReader.open(memoryIndex);
    searcher = new IndexSearcher(indexReader);
    int count = searcher.count(new TermQuery(new Term("_id", "http://url1.com")));
    assertEquals(1, count);
  }

  @Test
  void index_shouldNotDuplicateDocuments() throws IOException {
    IndexDocument iDocument1 = new IndexDocument("http://url1.com", "doc1", "doc1 content");
    indexer.index(iDocument1);
    indexer.index(iDocument1);
    indexReader = DirectoryReader.open(memoryIndex);
    searcher = new IndexSearcher(indexReader);
    int count = searcher.count(new TermQuery(new Term("_id", "http://url1.com")));
    assertEquals(1, count);
  }

  @Test
  void testIndex() {
  }
}