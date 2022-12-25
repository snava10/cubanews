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
    SqliteMetadataDatabase metadataDatabase = new SqliteMetadataDatabase(metadataDbName, "metaTable");
    metadataDatabase.initialise();
    controller.start(2, 10, Stream.of(
            "https://www.cibercuba.com/noticias/2022-12-25-u1-e208512-s27061-cuentas-cubanas-cuc-seran-historia-proximo-miercoles"
        ).collect(Collectors.toSet()), new LuceneIndexer(indexPath), metadataDatabase)
        .doOnError(error -> {
          System.out.println(error.getLocalizedMessage());
          System.exit(1);
        }).blockingSubscribe();
  }
}
