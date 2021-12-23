package com.snava.crawlandsearch;

import edu.uci.ics.crawler4j.crawler.Page;
import java.util.List;

public interface Crawler {

  void crawl(List<String> baseUrls, String indexName);

}
