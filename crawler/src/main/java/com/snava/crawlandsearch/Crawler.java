package com.snava.crawlandsearch;

import io.reactivex.Observable;
import java.util.Set;

public interface Crawler {

  Observable<Object> start(int maxPagesToFetch, int numCrawlers, Set<String> baseUrls,
      String indexPath) throws Exception;

}
