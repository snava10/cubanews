import static org.assertj.core.api.Assertions.assertThat;

import com.snava.cubanews.CrawlRequestData;
import com.snava.cubanews.Crawler;
import com.snava.cubanews.CrawlerController;
import com.snava.cubanews.DeletePagesManager;
import com.snava.cubanews.DocumentState;
import com.snava.cubanews.ImmutableCrawlRequestData;
import com.snava.cubanews.LuceneIndexer;
import com.snava.cubanews.Searcher;
import com.snava.cubanews.Searcher.SearcherResult;
import com.snava.cubanews.TestHttpServer;
import com.snava.cubanews.data.access.SqliteMetadataDatabase;
import com.snava.cubanews.data.access.SqliteMigrationManager;
import io.reactivex.observers.TestObserver;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IntegrationTest {

  static TestHttpServer testHttpServer;
  Crawler crawler;
  static SqliteMetadataDatabase db;
  static String currentDir = System.getProperty("user.dir");
  static Path crawlersDir = Path.of(currentDir + "/crawlers");


  @BeforeAll
  public static void beforeAll() throws Exception {
    // Create directories
    FileUtils.deleteDirectory(crawlersDir.toFile());
    Files.createDirectory(crawlersDir);
    Files.createDirectory(Path.of(crawlersDir + "/crawler4j"));
    Files.createDirectory(Path.of(crawlersDir + "/metadata"));

    // Start the test http server
    testHttpServer = new TestHttpServer();
    testHttpServer.start();
  }

  @AfterAll
  public static void afterAll() throws IOException {
    testHttpServer.stop();
    Path crawlersDir = Path.of(currentDir + "/crawlers");
    FileUtils.deleteDirectory(crawlersDir.toFile());
  }

  @BeforeEach
  public void beforeEach() {
    db = new SqliteMetadataDatabase(crawlersDir + "/metadata/" + "cubanews.db", "pages");
    Path resourceDirectory = Paths.get("src", "test", "resources", "db_migrations");
    String migrationsDirectory = resourceDirectory.toFile().getAbsolutePath();
    SqliteMigrationManager migrationManager = new SqliteMigrationManager(db, migrationsDirectory);
    try {
      db.initialise();
      migrationManager.runMigrations();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    crawler = new CrawlerController(currentDir + "/crawlers/crawler4j");
  }

  private CrawlRequestData triggerCrawling() throws Exception {
    CrawlRequestData crawlRequestData = ImmutableCrawlRequestData.builder()
        .seeds(List.of("http://localhost:4001/news/cuba/cuba.html"))
        .baseUrls(List.of("http://localhost:4001"))
        .indexName("cuba")
        .sourceName("cuba")
        .depth(2)
        .limit(-1)
        .build();
    LuceneIndexer indexer =
        new LuceneIndexer(crawlersDir + "/" + crawlRequestData.indexName(), crawlRequestData.indexName());
    crawler.start(crawlRequestData.limit(), crawlRequestData.depth(), 1,
        crawlRequestData.seeds(), crawlRequestData.baseUrls(), indexer, db).blockingAwait();
    return crawlRequestData;
  }

  @Test
  public void testCrawler() throws Exception {
    triggerCrawling();
    Searcher searcher = new Searcher();
    Directory index = FSDirectory.open(Paths.get(crawlersDir + "/cuba"));
    List<SearcherResult> result = searcher.search("cuba", index, 1000);
    assertThat(result.size()).isEqualTo(2);
    assertThat(db.count()).isEqualTo(2);
  }
  @Test
  public void testSearcher() throws Exception {
    triggerCrawling();
    Searcher searcher = new Searcher();
    Directory index = FSDirectory.open(Paths.get(crawlersDir + "/cuba"));
    List<SearcherResult> result = searcher.search("Cristobal Colon", index, 1000);
    assertThat(result.size()).isEqualTo(2);
    assertThat(result.get(0).doc().get("title")).contains("Cristobal Colon");
  }
  @Test
  public void testRemoveOld() throws Exception {
    CrawlRequestData crawlRequestData = triggerCrawling();
    TestObserver<Integer> testObserver = new TestObserver<>();
    LuceneIndexer deleteOldIndexer =
        new LuceneIndexer(crawlersDir + "/" + crawlRequestData.indexName(), crawlRequestData.indexName());
    DeletePagesManager.deleteOldPageReactive(1, TimeUnit.MILLISECONDS, db, deleteOldIndexer)
        .subscribe(testObserver);
    testObserver.assertNoErrors();
    testObserver.assertComplete();
    testObserver.assertValue(2);
    Searcher searcher = new Searcher();
    Directory index = FSDirectory.open(Paths.get(crawlersDir + "/cuba"));
    List<SearcherResult> result = searcher.search("cuba", index, 1000);
    assertThat(result.size()).isEqualTo(0);
    assertThat(db.count(DocumentState.DELETED)).isEqualTo(2);
    assertThat(db.count(DocumentState.ACTIVE)).isEqualTo(0);
  }
}
