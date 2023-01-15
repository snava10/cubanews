package com.snava.cubanews;

import com.snava.cubanews.data.access.SqliteMetadataDatabase;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import io.reactivex.Observable;
import java.io.File;
import java.util.Set;

public class CrawlerController implements Crawler {

  CrawlController crawlController;
  File crawlStorage;

  public CrawlerController(String crawlerLocalDataPath) {
    crawlStorage = new File(crawlerLocalDataPath);
  }

  public Observable<Object> start(int maxPagesToFetch, int numCrawlers, Set<String> baseUrls,
      Indexer indexer, SqliteMetadataDatabase metadataDatabase)
      throws Exception {
    CrawlConfig config = new CrawlConfig();
    // TODO: Add config for hard coded parameter
    config.setCrawlStorageFolder("/tmp");
    config.setMaxPagesToFetch(maxPagesToFetch);
    PageFetcher pageFetcher = new PageFetcher(config);
    RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
    RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
    crawlController = new CrawlController(config, pageFetcher, robotstxtServer);
    baseUrls.forEach(url -> crawlController.addSeed(url));
    Operation crawlOperation = ImmutableOperation.builder().type(OperationType.CRAWL)
        .state(OperationState.IN_PROGRESS).build();
    CrawlController.WebCrawlerFactory<HtmlCrawler> factory = () -> new HtmlCrawler(indexer,
        baseUrls, metadataDatabase, crawlOperation);

    return Observable.fromCallable(() -> {
      try {
        metadataDatabase.insertOperation(crawlOperation);
        crawlController.start(factory, numCrawlers);
      } catch (Exception ex) {
        return Observable.error(ex);
      }
      return crawlOperation;
    }).doOnNext(o -> {
      System.out.println("Closing index writer");
      indexer.close();
      Operation op = (Operation)o;
      metadataDatabase.completeOperation(op);
    });
  }
}
