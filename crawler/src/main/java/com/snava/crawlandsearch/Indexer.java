package com.snava.crawlandsearch;

import java.io.IOException;
import java.util.List;

public interface Indexer {
  void index(IndexDocument doc) throws IOException;
  void index(List<IndexDocument> doc) throws IOException;
  void close() throws IOException;
}
