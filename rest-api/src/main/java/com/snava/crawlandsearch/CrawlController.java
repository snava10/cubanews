package com.snava.crawlandsearch;

import io.reactivex.schedulers.Schedulers;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class CrawlController {

  @Autowired
  Crawler crawler;

  @Deprecated
  @GetMapping("/api/crawl/{indexId}")
  public CrawlResponse crawl(@PathVariable String indexId) throws Exception {
    System.out.printf("Starting to crawl %s", indexId);
    // TODO: Create configs for the hard coded parameters
    crawler.start(Stream.of(
                "https://adncuba.com/noticias-de-cuba",
                "https://www.14ymedio.com/",
                "https://www.cibercuba.com/noticias"
            ).collect(Collectors.toSet()), new LuceneIndexer("/tmp/" + indexId)
        ).doOnError(error -> System.out.println(error.getLocalizedMessage()))
        .subscribeOn(Schedulers.io()).subscribe();
    return new CrawlResponse(indexId);
  }

  @PostMapping("/api/crawl")
  public Mono<CrawlResponse> crawl(@RequestBody CrawlRequest crawlRequest) throws Exception {
    System.out.println(crawlRequest);
    crawler.start(crawlRequest.getLimit(), 12, crawlRequest.getBaseUrls(),
        new LuceneIndexer("/tmp/" + crawlRequest.getIndexName())).subscribeOn(Schedulers.io()).subscribe();
    return Mono.just(new CrawlResponse(crawlRequest.getIndexName()));
  }

}
