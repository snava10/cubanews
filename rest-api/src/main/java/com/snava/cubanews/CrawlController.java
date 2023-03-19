package com.snava.cubanews;

import com.snava.cubanews.data.access.SqliteMetadataDatabase;
import com.snava.cubanews.data.models.CrawlRequest;
import com.snava.cubanews.data.models.LongRunningOperationResponse;
import com.snava.cubanews.data.models.LongRunningOperationResponse.OperationStatus;
import com.snava.cubanews.data.models.LongRunningOperationResponse.OperationType;
import io.reactivex.Flowable;
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

  RatedLogger logger = new RatedLogger(SearchController.class, 0, 1, 1);

  @PostMapping("/api/crawl")
  public Mono<LongRunningOperationResponse> crawl(
      @RequestParam(value = "dryRun", defaultValue = "false") boolean dryRun,
      @RequestBody CrawlRequest crawlRequest)
      throws Exception {
    System.out.println(crawlRequest);
    if (!dryRun) {
      crawler.start(crawlRequest.getLimit(), -1, 12, crawlRequest.getSeeds(),
              crawlRequest.getBaseUrls(),
              new LuceneIndexer(homePath + crawlRequest.getIndexName(), crawlRequest.getIndexName()),
              db)
          .subscribeOn(Schedulers.io())
          .subscribe();
    }
    return Mono.just(
        new LongRunningOperationResponse(crawlRequest.getIndexName(), OperationStatus.IN_PROGRESS,
            OperationType.CRAWL));
  }

  @GetMapping("/api/clearold/{indexName}")
  public Mono<LongRunningOperationResponse> clearOld(@PathVariable String indexName,
      @RequestParam(value = "amount", defaultValue = "3") int amount,
      @RequestParam(value = "timeunit", defaultValue = "DAYS") String timeunit) throws IOException {
    System.out.printf("Deleting pages older that %d %s%n", amount, timeunit);
    Indexer indexer = new LuceneIndexer(homePath + indexName, indexName);
    DeletePagesManager.deleteOldPageReactive(amount, TimeUnit.valueOf(timeunit), db, indexer)
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

  @PostMapping("/api/crawl/{projectName}")
  public Mono<LongRunningOperationResponse> crawlProject(
      @PathVariable String projectName,
      @RequestParam(value = "dryRun", defaultValue = "false") boolean dryRun,
      @RequestBody com.snava.cubanews.CrawlRequest crawlRequest) throws Exception {
    logger.info(projectName);

    if (!dryRun) {
      for (CrawlRequestData crawlRequestData : crawlRequest.data()) {
        crawler.start(crawlRequestData.limit(), crawlRequestData.depth(), 12,
                crawlRequestData.seeds(), crawlRequestData.baseUrls(),
                new LuceneIndexer(homePath + crawlRequestData.indexName(),
                    crawlRequestData.indexName()), db)
            .subscribeOn(Schedulers.io()).subscribe();
      }
    }
    return Mono.just(
        new LongRunningOperationResponse(projectName,
            OperationStatus.IN_PROGRESS,
            OperationType.CRAWL)
    );
  }

}
