package com.snava.cubanews;

import com.snava.cubanews.data.access.SqliteMetadataDatabase;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class CrawlController {

  @Autowired
  String homePath;

  @Autowired
  Crawler crawler;

  @Autowired
  SqliteMetadataDatabase db;

  @PostMapping("/api/crawl")
  public Mono<CrawlResponse> crawl(@RequestBody CrawlRequest crawlRequest) throws Exception {
    System.out.println(crawlRequest);
    crawler.start(crawlRequest.getLimit(), 12, crawlRequest.getBaseUrls(),
            new LuceneIndexer(homePath + crawlRequest.getIndexName()), db).subscribeOn(Schedulers.io())
        .subscribe();
    return Mono.just(new CrawlResponse(crawlRequest.getIndexName()));
  }

  @GetMapping("/api/clearold/{indexName}")
  public Mono<Integer> clearOld(@PathVariable String indexName,
      @RequestParam(value = "amount", defaultValue = "24") int amount,
      @RequestParam(value = "timeunit", defaultValue = "HOURS") String timeunit) throws Exception {
    System.out.printf("Deleting pages older that %d %s%n", amount, timeunit);
    int result = DeletePagesManager.deleteOldPages(
        amount, TimeUnit.valueOf(timeunit), db, new LuceneIndexer(homePath + indexName)
    );
    return Mono.just(result);
  }

}
