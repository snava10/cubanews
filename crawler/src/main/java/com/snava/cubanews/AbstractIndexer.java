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

  public AbstractIndexer(String index) throws IOException {
    this(index, new StandardAnalyzer());
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

}
