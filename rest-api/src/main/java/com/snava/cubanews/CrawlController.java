package com.snava.cubanews;

import com.snava.cubanews.data.access.SqliteMetadataDatabase;
import io.reactivex.schedulers.Schedulers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class CrawlController {

  @Autowired
  String homePath;

  @Autowired
  Crawler crawler;

  @Autowired
  SqliteMetadataDatabase metadataDatabase;

  @PostMapping("/api/crawl")
  public Mono<CrawlResponse> crawl(@RequestBody CrawlRequest crawlRequest) throws Exception {
    System.out.println(crawlRequest);
    crawler.start(crawlRequest.getLimit(), 12, crawlRequest.getBaseUrls(),
            new LuceneIndexer(homePath + crawlRequest.getIndexName()), metadataDatabase).subscribeOn(Schedulers.io())
        .subscribe();
    return Mono.just(new CrawlResponse(crawlRequest.getIndexName()));
  }

}
