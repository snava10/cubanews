package com.snava.crawlandsearch;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import java.io.File;

public class Program {

  public static void main(String[] args) throws Exception {
    File crawlStorage = new File("local-data/crawler4j");
    CrawlConfig config = new CrawlConfig();
    config.setCrawlStorageFolder(crawlStorage.getAbsolutePath());
    config.setMaxPagesToFetch(10);

    int numCrawlers = 1;

    PageFetcher pageFetcher = new PageFetcher(config);
    RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
    RobotstxtServer robotstxtServer= new RobotstxtServer(robotstxtConfig, pageFetcher);
    CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

    controller.addSeed("https://adncuba.com/noticias-de-cuba");

    LuceneIndexer luceneIndexer = new LuceneIndexer("local-data/index");
    CrawlController.WebCrawlerFactory<HtmlCrawler> factory = () -> new HtmlCrawler(luceneIndexer);
    controller.start(factory, numCrawlers);
  }

}
