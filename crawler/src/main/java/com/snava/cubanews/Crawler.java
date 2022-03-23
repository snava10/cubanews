package com.snava.cubanews;

import com.snava.cubanews.data.access.SqliteMetadataDatabase;
import io.reactivex.Observable;
import java.util.Set;

public interface Crawler {

  Observable<Object> start(int maxPagesToFetch, int numCrawlers, Set<String> baseUrls,
      Indexer indexer, SqliteMetadataDatabase metadataDatabase, PagesHashing pagesHashing) throws Exception;

  default Observable<Object> start(Set<String> baseUrls, Indexer indexer,
      SqliteMetadataDatabase metadataDatabase, PagesHashing pagesHashing)
      throws Exception {
    return start(Integer.MAX_VALUE, 12, baseUrls, indexer, metadataDatabase, pagesHashing);
  }

}
