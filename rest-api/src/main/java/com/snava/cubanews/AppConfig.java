package com.snava.cubanews;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.snava.cubanews.data.access.SqliteMetadataDatabase;
import com.snava.cubanews.data.access.SqliteMigrationManager;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AppConfig {

  private static final String APP_DATA_DIR = "crawlers";

  @Value("${spring.profiles.active:test}")
  private String activeProfile;

  @Bean
  public String homePath() {
    try {
      String path = System.getProperty("user.home") + "/" + APP_DATA_DIR + "/";
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
  public String migrationsDirectory() {
    String path = System.getProperty("user.home") + "/db_migrations";
    File theDir = new File(path);
    if (!theDir.exists()){
      theDir.mkdirs();
    }
    return path;
  }

  @Bean
  @Profile("dev")
  public String migrationsDirectoryDev() {
    Path resourceDirectory = Paths.get("rest-api","src","main","resources", "db_migrations");
    return resourceDirectory.toFile().getAbsolutePath();
  }

  @Bean
  @Profile("test")
  public String migrationsDirectoryTest() {
    Path resourceDirectory = Paths.get("src","main","resources", "db_migrations");
    return resourceDirectory.toFile().getAbsolutePath();
  }

  @Bean
  public Searcher searcher() {
    return new Searcher();
  }

  @Bean
  public Crawler crawler() {
    return new CrawlerController(homePath() + "crawler4j");
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
    SqliteMetadataDatabase db =
        new SqliteMetadataDatabase(databasePath + "cubanews.db", metadataTableName());
    String migrationsDirectory =
        activeProfile.equals("dev") ? migrationsDirectoryDev() : activeProfile.equals("test") ? migrationsDirectoryTest() : migrationsDirectory();
    SqliteMigrationManager migrationManager = new SqliteMigrationManager(db, migrationsDirectory);
    try {
      db.initialise();
      migrationManager.runMigrations();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return db;
  }
}
