package com.snava.cubanews;

import com.snava.cubanews.data.access.SqliteMetadataDatabase;
import com.snava.cubanews.data.models.CrawlRequest;
import com.snava.cubanews.data.models.LongRunningOperationResponse;
import com.snava.cubanews.data.models.LongRunningOperationResponse.OperationStatus;
import com.snava.cubanews.data.models.LongRunningOperationResponse.OperationType;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
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
  public Mono<LongRunningOperationResponse> crawl(@RequestBody CrawlRequest crawlRequest)
      throws Exception {
    System.out.println(crawlRequest);
    crawler.start(crawlRequest.getLimit(), 12, crawlRequest.getBaseUrls(),
            new LuceneIndexer(homePath + crawlRequest.getIndexName()), db).subscribeOn(Schedulers.io())
        .subscribe();
    return Mono.just(
        new LongRunningOperationResponse(crawlRequest.getIndexName(), OperationStatus.IN_PROGRESS,
            OperationType.CRAWL));
  }

  @GetMapping("/api/clearold/{indexName}")
  public Mono<LongRunningOperationResponse> clearOld(@PathVariable String indexName,
      @RequestParam(value = "amount", defaultValue = "3") int amount,
      @RequestParam(value = "timeunit", defaultValue = "DAYS") String timeunit) throws Exception {
    System.out.printf("Deleting pages older that %d %s%n", amount, timeunit);

    Single.fromCallable(() -> clearOld(indexName, amount, TimeUnit.valueOf(timeunit)))
        .retryWhen(errors ->
            errors.zipWith(
                Flowable.range(1, 10),
                (throwable, retryCount) -> {
                  System.out.printf("Retrying %d%n", retryCount);
                  return retryCount;
                }
            ).flatMap(retryCount -> {
              long wait = (long) Math.pow(2, retryCount);
              System.out.printf("Waiting for %d seconds%n", wait);
              return Flowable.timer(wait, TimeUnit.SECONDS);
            })
        )
        .subscribeOn(Schedulers.io()).subscribe();

    return Mono.just(new LongRunningOperationResponse(indexName, OperationStatus.IN_PROGRESS,
        OperationType.DELETE_OLD_PAGES));
  }

  private int clearOld(String indexName, int amount, TimeUnit timeUnit) throws IOException {
    Indexer indexer = new LuceneIndexer(homePath + indexName);
    return DeletePagesManager.deleteOldPages(amount, timeUnit, db, indexer);
  }

}
