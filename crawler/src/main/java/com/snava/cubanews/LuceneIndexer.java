package com.snava.cubanews;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

public class LuceneIndexer extends AbstractIndexer {

  IndexSearcher searcher;

  public LuceneIndexer(String index) throws IOException {
    super(index);
  }

  public LuceneIndexer(String index, Analyzer analyzer)
      throws IOException {
    super(index, analyzer);
  }

  public LuceneIndexer(Directory directory, Analyzer analyzer) throws IOException {
    super(directory, analyzer);
  }

  private IndexSearcher getIndexSearcher() throws IOException {
    if (searcher != null) {
      return this.searcher;
    }
    IndexReader indexReader = DirectoryReader.open(index);
    searcher = new IndexSearcher(indexReader);
    return searcher;
  }

  private void saveDocument(IndexDocument doc) throws IOException {
    Document document = new Document();
    document.add(new StringField("_id", Objects.requireNonNull(doc.url()), Store.NO));
    document.add(new TextField("title", Objects.requireNonNull(doc.title()), Store.YES));
    document.add(new TextField("url", Objects.requireNonNull(doc.url()), Store.YES));
    document.add(new TextField("text", Objects.requireNonNull(doc.text()), Store.YES));
    document.add(new StringField("lastUpdated", String.valueOf(doc.lastUpdated()), Store.YES));
    Term term = new Term("_id", Objects.requireNonNull(doc.url()));
    TermQuery termQuery = new TermQuery(term);

    TopDocs results =
        DirectoryReader.indexExists(index) ? getIndexSearcher().search(termQuery, 1) : null;

    if (results != null && results.totalHits.value > 0) {
      writer.updateDocument(term, document);
    } else {
      // TODO: Log the exception
      // This exception means that this is the first document to be added to the index.
      writer.addDocument(document);
    }
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
      System.out.printf("%s %s", document.url(), document.title());
    }
    writer.commit();
  }

  @Override
  public void delete(List<String> docUrls) throws IOException {
    List<Term> terms = docUrls.stream().map(url -> new Term("_id", Objects.requireNonNull(url))).collect(
        Collectors.toList());
    Term[] termsArray = new Term[terms.size()];
    terms.toArray(termsArray);
    writer.deleteDocuments(termsArray);
    writer.commit();
  }
}
