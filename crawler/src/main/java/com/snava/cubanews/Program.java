package com.snava.cubanews;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is for testing purposes. Not to be used in production.
 */
public class Program {

  public static void main(String[] args) throws Exception {

    String projectId = "crawl-and-search";
    GoogleCredentials credentials = GoogleCredentials.fromStream(
            new FileInputStream("/etc/datastore/datastore-key"))
        .createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));
    FirestoreOptions firestoreOptions =
        FirestoreOptions.getDefaultInstance().toBuilder()
            .setProjectId(projectId)
            .setCredentials(credentials)
            .build();
    Firestore db = firestoreOptions.getService();

    CrawlerController controller = new CrawlerController("/tmp/crawler4j");
    controller.start(100, 10, Stream.of(
            "https://adncuba.com/noticias-de-cuba",
            "https://www.14ymedio.com/",
            "https://www.cibercuba.com/noticias"
        ).collect(Collectors.toSet()), new LuceneIndexer("/tmp/cubanews"), db)
        .doOnError(error -> {
          System.out.println(error.getLocalizedMessage());
          System.exit(1);
        }).blockingSubscribe();
  }

}
