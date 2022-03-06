package com.snava.cubanews;

import com.google.cloud.firestore.Firestore;
import io.reactivex.Observable;
import java.util.Set;

public interface Crawler {

  Observable<Object> start(int maxPagesToFetch, int numCrawlers, Set<String> baseUrls,
      Indexer indexer, Firestore db) throws Exception;

  default Observable<Object> start(Set<String> baseUrls, Indexer indexer, Firestore db)
      throws Exception {
    return start(Integer.MAX_VALUE, 12, baseUrls, indexer, db);
  }

}
