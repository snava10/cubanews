package com.snava.cubanews;

import java.io.IOException;
import java.nio.file.Paths;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public abstract class AbstractIndexer implements Indexer {

  Directory index;
  Analyzer analyzer;
  IndexWriterConfig indexWriterConfig;
  IndexWriter writer;
  protected String indexName;
  protected RatedLogger logger;

  public AbstractIndexer(String indexPath, String indexName, Class clazz) throws IOException {
    this(indexPath, new StandardAnalyzer());
    logger = new RatedLogger(clazz);
    this.indexName = indexName;
  }

  public AbstractIndexer(String index, Analyzer analyzer) throws IOException {
    this(FSDirectory.open(Paths.get(index)), analyzer);
  }

  public AbstractIndexer(Directory directory, Analyzer analyzer) throws IOException {
    this.index = directory;
    this.analyzer = analyzer;
    this.indexWriterConfig = new IndexWriterConfig(analyzer);
    this.writer = new IndexWriter(this.index, indexWriterConfig);
  }

  @Override
  public synchronized void close() throws IOException {
    if (writer.isOpen()) {
      writer.close();
    }
  }

  public boolean hasDeletions() {
    return writer.hasDeletions();
  }

  public String getIndexName() {
    return indexName;
  }
}
