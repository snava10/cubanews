package com.snava.cubanews;

import com.google.cloud.firestore.Firestore;
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
      Indexer indexer, Firestore db)
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

    CrawlController.WebCrawlerFactory<HtmlCrawler> factory = () -> new HtmlCrawler(indexer,
        baseUrls, db);
    return Observable.fromCallable(() -> {
      try {
        crawlController.start(factory, numCrawlers);
      } catch (Exception ex) {
        return Observable.error(ex);
      }
      return true;
    }).doOnNext(o -> {
      System.out.println("Closing index writer");
      indexer.close();

    });
  }
}
