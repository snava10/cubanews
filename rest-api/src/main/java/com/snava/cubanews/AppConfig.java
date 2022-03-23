package com.snava.cubanews;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.snava.cubanews.data.access.SqliteMetadataDatabase;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AppConfig {

  @Bean
  public String homePath() {
    try {
      String path = "/data/";
      Files.createDirectories(Paths.get(path));
      return path;
    } catch (Exception e) {
      throw new RuntimeException("Error creating home folder", e);
    }
  }

  @Bean
  public String metadataTableName() {
    return "pages";
  }

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
  SqliteMetadataDatabase metadataDatabase() throws IOException {
    String databasePath = homePath() + "metadata/";
    Files.createDirectories(Paths.get(databasePath));
    SqliteMetadataDatabase db = new SqliteMetadataDatabase(databasePath + "cubanews.db", metadataTableName());
    try {
      db.initialise();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return db;
  }

  @Bean
  PagesHashing pagesHashing(SqliteMetadataDatabase db) {
    return new PagesHashing(db);
  }
}
