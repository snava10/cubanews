package com.snava.cubanews;

import com.snava.cubanews.data.access.SqliteMetadataDatabase;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import io.reactivex.Completable;
import io.reactivex.Observable;
import java.io.File;
import java.util.Set;

public class CrawlerController implements Crawler {

  CrawlController crawlController;
  File crawlStorage;
  RatedLogger logger;

  public CrawlerController(String crawlerLocalDataPath, RatedLogger logger) {
    crawlStorage = new File(crawlerLocalDataPath);
    this.logger = logger;
  }

  public CrawlerController(String crawlerLocalDataPath) {
    this(crawlerLocalDataPath, new RatedLogger(CrawlerController.class));
  }

  @Override
  public Completable start(int maxPagesToFetch, int depth, int numCrawlers, Set<String> seedUrls, Set<String> baseUrls,
      Indexer indexer, SqliteMetadataDatabase metadataDatabase)
      throws Exception {
    CrawlConfig config = new CrawlConfig();
    // TODO: Add config for hard coded parameter
    config.setCrawlStorageFolder("/tmp/" + indexer.getIndexName());
    config.setMaxPagesToFetch(maxPagesToFetch);
    config.setMaxDepthOfCrawling(depth);
    return start(config, numCrawlers, seedUrls, baseUrls, indexer, metadataDatabase);
  }

  @Override
  public Completable start(CrawlConfig config, int numCrawlers, Set<String> seedUrls, Set<String> baseUrls,
      Indexer indexer, SqliteMetadataDatabase metadataDatabase) throws Exception {
    PageFetcher pageFetcher = new PageFetcher(config);
    RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
    RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
    crawlController = new CrawlController(config, pageFetcher, robotstxtServer);
    seedUrls.forEach(url -> crawlController.addSeed(url));
    Operation crawlOperation = ImmutableOperation.builder().type(OperationType.CRAWL)
        .state(OperationState.IN_PROGRESS).build();
    CrawlController.WebCrawlerFactory<HtmlCrawler> factory = () -> new HtmlCrawler(indexer,
        seedUrls, baseUrls, metadataDatabase, crawlOperation);

    return Completable.fromObservable(Observable.fromCallable(() -> {
      try {
        metadataDatabase.insertOperation(crawlOperation);
        crawlController.start(factory, numCrawlers);
      } catch (Exception ex) {
        return Observable.error(ex);
      }
      return crawlOperation;
    }).doOnNext(o -> {
      logger.info("Closing index writer");
      indexer.close();
      Operation op = (Operation) o;
      metadataDatabase.completeOperation(op);
    }));
  }
}
