package com.snava.crawlandsearch;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CrawlController {

  @Autowired
  Crawler crawler;

  @GetMapping("/crawl/{crawlId}")
  public CrawlResponse crawl(@PathVariable String crawlId) throws Exception {
    System.out.printf("Starting to crawl %s", crawlId);
    crawler.start(1000, 10,
            Stream.of(
                "https://adncuba.com/noticias-de-cuba",
                "https://www.14ymedio.com/",
                "https://www.cibercuba.com/noticias"
            ).collect(Collectors.toSet()), "local-data/index"
        ).doOnError(error -> System.out.println(error.getLocalizedMessage()))
        .subscribeOn(Schedulers.io());
    return new CrawlResponse(crawlId);
  }

}
