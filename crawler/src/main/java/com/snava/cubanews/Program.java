package com.snava.cubanews;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is for testing purposes. Not to be used in production.
 */
public class Program {

  public static void main(String[] args) throws Exception {

    CrawlerController controller = new CrawlerController("/tmp/crawler4j");
    controller.start(10, 10, Stream.of(
            "https://adncuba.com/noticias-de-cuba",
            "https://www.14ymedio.com/",
            "https://www.cibercuba.com/noticias"
        ).collect(Collectors.toSet()), new LuceneIndexer("/tmp/cubanews"))
        .doOnError(error -> {
          System.out.println(error.getLocalizedMessage());
          System.exit(1);
        }).blockingSubscribe();
  }

}
