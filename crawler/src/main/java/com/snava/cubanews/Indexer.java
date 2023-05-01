package com.snava.cubanews;

import java.io.IOException;
import java.util.List;

public interface Indexer {
  void index(IndexDocument doc) throws IOException;
  void index(List<IndexDocument> doc) throws IOException;
  void delete(List<String> docUrls) throws IOException;
  void close() throws IOException;
  String getIndexName();
  String getIndexPath();
  String getProjectAndIndexName();
}
