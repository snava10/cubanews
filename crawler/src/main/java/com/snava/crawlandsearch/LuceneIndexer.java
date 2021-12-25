package com.snava.crawlandsearch;

import java.io.IOException;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;

public class LuceneIndexer extends AbstractIndexer {

  public LuceneIndexer(String index) throws IOException {
    super(index);
  }

  public LuceneIndexer(String index, Analyzer analyzer)
      throws IOException {
    super(index, analyzer);
  }

  private void saveDocument(IndexDocument doc) throws IOException {
    Document document = new Document();
    document.add(new StringField("_id", doc.getUrl(), Store.NO));
    document.add(new TextField("title", doc.getTitle(), Store.YES));
    document.add(new TextField("url", doc.getUrl(), Store.YES));
    document.add(new TextField("text", doc.getText(), Store.YES));
    document.add(new LongPoint("lastUpdated", doc.getLastUpdated()));
    Term term = new Term("_id", doc.getUrl());
    writer.updateDocument(term, document);
  }

  @Override
  public void index(IndexDocument doc) throws IOException {
    saveDocument(doc);
    writer.commit();
  }

  @Override
  public void index(List<IndexDocument> documents) throws IOException {
    for (IndexDocument document : documents) {
      saveDocument(document);
      System.out.printf("%s %s", document.getUrl(), document.getTitle());
    }
    writer.commit();
  }

}
