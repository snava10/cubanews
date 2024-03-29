package com.snava.cubanews;

import com.snava.cubanews.data.access.SqliteMetadataDatabase;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is for testing purposes. Not to be used in production.
 */
public class Program {

  public static void main(String[] args) throws Exception {
    final String metadataDbName = args[0];
    final String indexPath = args[1];

    CrawlerController controller = new CrawlerController("/tmp/crawler4j",
        new RatedLogger(Program.class, 1, 1, 1));
    SqliteMetadataDatabase metadataDatabase = new SqliteMetadataDatabase(metadataDbName,
        "metaTable");
    metadataDatabase.initialise();
    Set<String> seedUrls = Stream.of(
        "https://diariodecuba.com/",
        "https://adncuba.com/noticias-de-cuba",
        "https://www.14ymedio.com/",
        "https://www.cibercuba.com/noticias"
    ).collect(Collectors.toSet());
    controller.start(10, -1, 10, seedUrls, seedUrls,
            new LuceneIndexer(indexPath, indexPath.split("/")[1]), metadataDatabase)
        .doOnError(error -> {
          System.out.println(error.getLocalizedMessage());
          System.exit(1);
        }).blockingAwait();
  }
}
