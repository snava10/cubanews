package com.snava.cubanews;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AppConfig {

  @Bean
  public Searcher searcher() {
    return new Searcher();
  }

  @Bean
  public Crawler crawler() {
    return new CrawlerController("/tmp/crawler4j");
  }

  @Bean
  RouterFunction<ServerResponse> routerFunction() {
    return route(request -> !request.uri().getPath().startsWith("/api/") && !request.uri().getPath()
            .equals("/") && !request.uri().getPath().contains("."),
        request -> ServerResponse.temporaryRedirect(URI.create("/"))
            .build());
  }

  @Bean
  public Firestore firestore() throws IOException {
    String projectId = "crawl-and-search";
    GoogleCredentials credentials = GoogleCredentials.fromStream(
            new FileInputStream("/etc/datastore/datastore-key"))
        .createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));
    FirestoreOptions firestoreOptions =
        FirestoreOptions.getDefaultInstance().toBuilder()
            .setProjectId(projectId)
            .setCredentials(credentials)
            .build();
    return firestoreOptions.getService();
  }

  @Bean()
  public String collectionName() {
    return "pages";
  }

  @Bean
  public String countersCollectionName() {
    return "counters";
  }
}
