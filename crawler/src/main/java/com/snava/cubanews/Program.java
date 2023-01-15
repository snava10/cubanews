package com.snava.cubanews;

import com.snava.cubanews.data.access.SqliteMetadataDatabase;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is for testing purposes. Not to be used in production.
 */
public class Program {

  public static void main(String[] args) throws Exception {
    final String metadataDbName = args[0];
    final String indexPath = args[1];

    CrawlerController controller = new CrawlerController("/tmp/crawler4j");
    SqliteMetadataDatabase metadataDatabase = new SqliteMetadataDatabase(metadataDbName,
        "metaTable");
    metadataDatabase.initialise();
    controller.start(10, 10, Stream.of(
        "https://diariodecuba.com/",
            "https://adncuba.com/noticias-de-cuba",
            "https://www.14ymedio.com/",
            "https://www.cibercuba.com/noticias"
        ).collect(Collectors.toSet()), new LuceneIndexer(indexPath), metadataDatabase)
        .doOnError(error -> {
          System.out.println(error.getLocalizedMessage());
          System.exit(1);
        }).blockingSubscribe();
  }
}
