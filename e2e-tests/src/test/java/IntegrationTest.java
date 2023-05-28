import static org.assertj.core.api.Assertions.assertThat;

import com.snava.cubanews.CrawlRequestData;
import com.snava.cubanews.Crawler;
import com.snava.cubanews.CrawlerController;
import com.snava.cubanews.ImmutableCrawlRequestData;
import com.snava.cubanews.LuceneIndexer;
import com.snava.cubanews.TestHttpServer;
import com.snava.cubanews.data.access.SqliteMetadataDatabase;
import com.snava.cubanews.data.access.SqliteMigrationManager;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IntegrationTest {

  static TestHttpServer testHttpServer;
  Crawler crawler;
  static SqliteMetadataDatabase db;

  String homePath = Paths.get("").toFile().getAbsolutePath() + "/crawlers";

  @BeforeAll
  public static void beforeAll() throws Exception {
    // Start the test http server
    testHttpServer = new TestHttpServer();
    testHttpServer.start();

    // Initialise metadata db
    String databasePath = "metadata/";
    Files.createDirectories(Paths.get(databasePath));
    db = new SqliteMetadataDatabase(databasePath + "cubanews.db", "pages");
    Path resourceDirectory = Paths.get("src", "test", "resources", "db_migrations");
    String migrationsDirectory = resourceDirectory.toFile().getAbsolutePath();
    SqliteMigrationManager migrationManager = new SqliteMigrationManager(db, migrationsDirectory);
    try {
      db.initialise();
      migrationManager.runMigrations();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @AfterAll
  public static void afterAll() {
    testHttpServer.stop();
  }

  @BeforeEach
  public void beforeEach() {
    Path resourceDirectory = Paths.get("crawler-data");
    crawler = new CrawlerController(resourceDirectory.toFile().getAbsolutePath() + "/crawler4j");
  }

  @Test
  public void test() throws Exception {
    CrawlRequestData crawlRequestData = ImmutableCrawlRequestData.builder()
        .seeds(List.of("https://localhost:4001/cuba.html"))
        .baseUrls(List.of("https://localhost:4001/cuba.html"))
        .indexName("cuba")
        .sourceName("cuba")
        .depth(2)
        .limit(-1)
        .build();
    LuceneIndexer indexer =
        new LuceneIndexer(homePath + crawlRequestData.indexName(), crawlRequestData.indexName());
    crawler.start(crawlRequestData.limit(), crawlRequestData.depth(), 12,
        crawlRequestData.seeds(), crawlRequestData.baseUrls(), indexer, db).blockingAwait();

    assertThat(true).isTrue();
  }

  public void crawTest() throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:8080/api/crawl/cubanews_project"))
        .POST(HttpRequest.BodyPublishers.ofString(
            """
                {
                  "data": [
                      {
                        "seeds": [
                          "https://localhost:4001/cuba.html"
                        ],
                        "baseUrls": [
                          "https://localhost:4001/cuba.html"
                        ],
                        "indexName": "cuba",
                        "sourceName": "cuba",
                        "depth": 2
                      }
                    ]
                }
                """)).build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    assertThat(response.statusCode())
        .isEqualTo(200);
    assertThat(response.body())
        .isEqualTo("{\"message\":\"ok\"}");
  }

}
