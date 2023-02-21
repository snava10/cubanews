package com.snava.cubanews;

import com.snava.cubanews.data.access.SqliteMetadataDatabase;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import io.reactivex.Completable;
import java.util.Set;

public interface Crawler {

  Completable start(int maxPagesToFetch, int depth, int numCrawlers, Set<String> baseUrls,
      Indexer indexer, SqliteMetadataDatabase metadataDatabase) throws Exception;

  Completable start(CrawlConfig config, int numCrawlers, Set<String> baseUrls,
      Indexer indexer, SqliteMetadataDatabase metadataDatabase) throws Exception;

  default Completable start(Set<String> baseUrls, Indexer indexer,
      SqliteMetadataDatabase metadataDatabase)
      throws Exception {
    return start(-1, -1, 12, baseUrls, indexer, metadataDatabase);
  }

}
