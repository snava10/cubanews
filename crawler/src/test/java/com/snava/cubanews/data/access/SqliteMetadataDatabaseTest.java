package com.snava.cubanews.data.access;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.Tuple;
import com.snava.cubanews.DocumentState;
import com.snava.cubanews.ImmutableMetadataDocument;
import com.snava.cubanews.MetadataDocument;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SqliteMetadataDatabaseTest {

  static SqliteMetadataDatabase db;
  static String dbPath;
  static Random r = new Random();
  static String metaTable = "metaTable";

  @BeforeEach
  void setUp() throws SQLException {
    Path currentRelativePath = Paths.get("");
    String s = currentRelativePath.toAbsolutePath().toString();
    System.out.println("Current absolute path is: " + s);
    dbPath = s + "test" + r.nextInt(100000000) + ".db";
    db = new SqliteMetadataDatabase(dbPath, metaTable);
    db.initialise();
  }

  @AfterEach
  void tearDown() throws SQLException {
    File f = new File(dbPath);
    db.getConnection().close();
    assertThat(f.delete()).isTrue();
  }

  @Test
  void getUrl() {
  }

  @Test
  void getConnection() {
  }

  @Test
  void connect() {
    assertThat(db.getConnection()).isNotNull();
  }

  @Test
  void createMetadataTable() throws SQLException {
    String tableExistsSQL = String.format(
        "SELECT name FROM sqlite_master WHERE type='table' AND name='%s'", metaTable
    );
    String indexExistSQL = "SELECT name FROM sqlite_master WHERE type='index' AND name='idx_url'";
    Statement stmt = db.getConnection().createStatement();

    stmt.execute(tableExistsSQL);
    ResultSet resultSet = stmt.getResultSet();
    resultSet.next();
    assertThat(resultSet.getString("name")).isEqualTo(metaTable);

    stmt.execute(indexExistSQL);
    resultSet = stmt.getResultSet();
    resultSet.next();
    assertThat(resultSet.getString("name")).isEqualTo("idx_url");

    stmt.close();
  }

  @Test
  void getByUrlAndState() {
    MetadataDocument activeDoc = ImmutableMetadataDocument.builder().url("url1")
        .state(DocumentState.ACTIVE).createdAt(0L).lastUpdated(0L).build();
    MetadataDocument deletedDoc = ImmutableMetadataDocument.builder().url("url2")
        .state(DocumentState.DELETED).createdAt(0L).lastUpdated(0L).build();
    db.insertMany(Arrays.asList(activeDoc, deletedDoc));

    MetadataDocument activeDocReal = db.getByUrlAndState("url1",
        DocumentState.ACTIVE);
    activeDoc = ImmutableMetadataDocument.copyOf(activeDoc).withId(activeDocReal.id());
    assertThat(activeDocReal).isEqualTo(activeDoc);

    MetadataDocument deletedDocReal = db.getByUrlAndState("url2",
        DocumentState.DELETED);
    deletedDoc = ImmutableMetadataDocument.copyOf(deletedDoc).withId(deletedDocReal.id());
    assertThat(deletedDocReal).isEqualTo(deletedDoc);

    assertThat(db.getByUrlAndState("url1", DocumentState.DELETED)).isNull();
    assertThat(db.getByUrlAndState("urlx", DocumentState.ACTIVE)).isNull();
  }

  @Test
  void insertOne() {
    long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    MetadataDocument metadataDocument = ImmutableMetadataDocument.builder()
        .url("https://news.com/n1").state(DocumentState.ACTIVE).build();
    db.insertOne(metadataDocument);

    String sql = "Select * from " + metaTable;
    try (Statement stmt = db.getConnection().createStatement()) {
      ResultSet resultSet = stmt.executeQuery(sql);
      assertThat(resultSet.next()).isTrue();
      assertThat(resultSet.getString("url")).isEqualTo("https://news.com/n1");
      assertThat(resultSet.getInt("id")).isEqualTo(1);
      assertThat(resultSet.next()).isFalse();
    } catch (SQLException e) {
      e.printStackTrace();
      assertThat(true).isFalse();
    }
  }

  @Test
  void insertMany() {
    long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    MetadataDocument metadataDocument = ImmutableMetadataDocument.builder()
        .url("https://news.com/n1")
        .state(DocumentState.ACTIVE).build();
    MetadataDocument metadataDocument2 = ImmutableMetadataDocument.builder()
        .url("https://news.com/n2")
        .state(DocumentState.ACTIVE).build();

    db.insertMany(Arrays.asList(metadataDocument, metadataDocument2));

    String sql = "SELECT * FROM " + metaTable;
    try (Statement stmt = db.getConnection().createStatement()) {
      ResultSet resultSet = stmt.executeQuery(sql);
      assertThat(resultSet.next()).isTrue();
      assertThat(resultSet.getString("url")).isEqualTo("https://news.com/n1");
      assertThat(resultSet.getInt("id")).isEqualTo(1);

      assertThat(resultSet.next()).isTrue();
      assertThat(resultSet.getString("url")).isEqualTo("https://news.com/n2");
      assertThat(resultSet.getInt("id")).isEqualTo(2);

      assertThat(resultSet.next()).isFalse();
    } catch (SQLException e) {
      e.printStackTrace();
      assertThat(true).isFalse();
    }
  }

  @Test
  void updateState() throws InterruptedException {
    long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    Thread.sleep(1000);
    MetadataDocument metadataDocument = ImmutableMetadataDocument.builder()
        .url("https://news.com/n1")
        .state(DocumentState.ACTIVE).build();
    db.insertOne(metadataDocument);
    Thread.sleep(1000);
    db.updateState(metadataDocument.url(), DocumentState.DELETED);
    MetadataDocument md = db.getByUrlAndState(metadataDocument.url(),
        DocumentState.DELETED);
    assertThat(md).isNotNull();
    assertThat(md.lastUpdated()).isGreaterThan(timestamp);
    assertThat(md.lastUpdated()).isGreaterThan(md.createdAt());
  }

  @Test
  void updateManyStates() throws InterruptedException {
    long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    Thread.sleep(1000);
    MetadataDocument metadataDocument = ImmutableMetadataDocument.builder()
        .url("https://news.com/n1")
        .state(DocumentState.ACTIVE).build();
    MetadataDocument metadataDocument2 = ImmutableMetadataDocument.builder()
        .url("https://news.com/n2")
        .state(DocumentState.ACTIVE).build();
    List<Tuple<String, DocumentState>> list = Arrays.asList(
        Tuple.of(metadataDocument.url(), DocumentState.DELETED),
        Tuple.of(metadataDocument2.url(), DocumentState.DELETED)
    );

    db.insertMany(Arrays.asList(metadataDocument, metadataDocument2));
    db.updateManyStates(list);
    assertThat(db.getByUrlAndState(metadataDocument.url(),
        DocumentState.DELETED)).isNotNull();
    assertThat(db.getByUrlAndState(metadataDocument.url(),
        DocumentState.DELETED).lastUpdated()).isGreaterThan(timestamp);
    assertThat(db.getByUrlAndState(metadataDocument2.url(),
        DocumentState.DELETED)).isNotNull();
    assertThat(db.getByUrlAndState(metadataDocument2.url(),
        DocumentState.DELETED).lastUpdated()).isGreaterThan(timestamp);
  }

  @Test
  void updateStateByAge() throws Exception {
    long createdAt = LocalDateTime.now().minusHours(25).toEpochSecond(ZoneOffset.UTC);
    MetadataDocument metadataDocument = ImmutableMetadataDocument.builder()
        .url("https://news.com/n1")
        .state(DocumentState.ACTIVE).build();
    MetadataDocument metadataDocument2 = ImmutableMetadataDocument.builder()
        .url("https://news.com/n2")
        .state(DocumentState.ACTIVE).build();

    db.insertMany(Arrays.asList(metadataDocument, metadataDocument2));

    String updateSql = String.format("UPDATE %s SET createdAt=%d where url='%s'", metaTable,
        createdAt, metadataDocument.url());
    Statement stmt = db.getConnection().createStatement();
    stmt.executeUpdate(updateSql);
    stmt.close();

    int result = db.updateStateByAge(24, TimeUnit.HOURS, DocumentState.DELETED);
    assertThat(result).isEqualTo(1);
    assertThat(db.getByUrl(metadataDocument.url()).get().state()).isEqualTo(DocumentState.DELETED);
    assertThat(db.getByUrl(metadataDocument2.url()).get().state()).isEqualTo(DocumentState.ACTIVE);
  }

  @Test
  void getDeletablePages() throws SQLException {
    long createdAt = LocalDateTime.now().minusHours(25).toEpochSecond(ZoneOffset.UTC);
    for (int i = 0; i < 50; i++) {
      MetadataDocument metadataDocument = ImmutableMetadataDocument.builder()
          .url("https://news.com/n" + i)
          .state(DocumentState.ACTIVE).build();
      db.insertOne(metadataDocument);
    }
    String updateSql = String.format("UPDATE %s SET createdAt=%d where state='ACTIVE'", metaTable,
        createdAt);
    Statement stmt = db.getConnection().createStatement();
    stmt.executeUpdate(updateSql);

    for (int i = 50; i < 100; i++) {
      MetadataDocument metadataDocument = ImmutableMetadataDocument.builder()
          .url("https://news.com/n" + i)
          .state(DocumentState.ACTIVE).build();
      db.insertOne(metadataDocument);
    }

    int batches = 0;
    for (List<String> urls: db.getDeletablePages(24, TimeUnit.HOURS, 10)) {
      assertThat(urls.contains("https://news.com/n" + (batches * 10))).isTrue();
      batches++;
    }
    assertThat(batches).isEqualTo(5);
  }

}