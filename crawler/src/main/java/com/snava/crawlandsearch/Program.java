package com.snava.crawlandsearch;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Program {

  public static void main(String[] args) throws Exception {

    CrawlerController controller = new CrawlerController("local-data/crawler4j");
    controller.start(1000, 10, Stream.of(
            "https://adncuba.com/noticias-de-cuba",
            "https://www.14ymedio.com/",
            "https://www.cibercuba.com/noticias"
        ).collect(Collectors.toSet()), "local-data/index")
        .doOnError(error -> {
          System.out.println(error.getLocalizedMessage());
          System.exit(1);
        }).blockingSubscribe();
  }

}
