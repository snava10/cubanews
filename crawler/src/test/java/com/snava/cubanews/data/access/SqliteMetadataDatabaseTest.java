package com.snava.cubanews.data.access;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.Tuple;
import com.snava.cubanews.DocumentState;
import com.snava.cubanews.ImmutableMetadataDocument;
import com.snava.cubanews.ImmutableOperation;
import com.snava.cubanews.MetadataDocument;
import com.snava.cubanews.Operation;
import com.snava.cubanews.OperationState;
import com.snava.cubanews.OperationType;
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
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SqliteMetadataDatabaseTest {

  static SqliteMetadataDatabase db;
  static String dbPath;
  static Random r = new Random();
  static String metaTable = "metaTable";
  static String operationsTable = "operations";

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

  @ParameterizedTest
  @ValueSource(strings = {"metaTable", "operations"})
    // six numbers
  void createMetadataTable(String tableName) throws SQLException {
    String tableExistsSQL = String.format(
        "SELECT name FROM sqlite_master WHERE type='table' AND name='%s'", tableName);
    String indexExistSQL = "SELECT name FROM sqlite_master WHERE type='index' AND name='idx_url'";
    Statement stmt = db.getConnection().createStatement();

    stmt.execute(tableExistsSQL);
    ResultSet resultSet = stmt.getResultSet();
    resultSet.next();
    assertThat(resultSet.getString("name")).isEqualTo(tableName);

    if (Objects.equals(tableName, metaTable)) {
      stmt.execute(indexExistSQL);
      resultSet = stmt.getResultSet();
      resultSet.next();
      assertThat(resultSet.getString("name")).isEqualTo("idx_url");
    }
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
    assertThat(activeDocReal.url()).isEqualTo(activeDoc.url());

    MetadataDocument deletedDocReal = db.getByUrlAndState("url2",
        DocumentState.DELETED);
    assertThat(deletedDocReal.url()).isEqualTo(deletedDoc.url());

    assertThat(db.getByUrlAndState("url1", DocumentState.DELETED)).isNull();
    assertThat(db.getByUrlAndState("urlx", DocumentState.ACTIVE)).isNull();
  }

  @Test
  void insertOne() {
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
        Tuple.of(metadataDocument2.url(), DocumentState.DELETED));

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

    int result = db.updateStateByAgeAndState(24, TimeUnit.HOURS, DocumentState.ACTIVE,
        DocumentState.DELETED);
    assertThat(result).isEqualTo(1);
    assertThat(db.getByUrl(metadataDocument.url()).orElseThrow().state()).isEqualTo(
        DocumentState.DELETED);
    assertThat(db.getByUrl(metadataDocument2.url()).orElseThrow().state()).isEqualTo(
        DocumentState.ACTIVE);
  }

  @Test
  void getDeletablePages() {
    long createdAt = LocalDateTime.now().minusHours(25).toEpochSecond(ZoneOffset.UTC);
    for (int i = 0; i < 50; i++) {
      MetadataDocument metadataDocument = ImmutableMetadataDocument.builder()
          .url("https://news.com/n" + i)
          .state(DocumentState.ACTIVE).build();
      db.insertOne(metadataDocument);
    }
    String updateSql = String.format("UPDATE %s SET createdAt=%d where state='ACTIVE'", metaTable,
        createdAt);
    try (Statement stmt = db.getConnection().createStatement()) {
      stmt.executeUpdate(updateSql);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    for (int i = 50; i < 100; i++) {
      MetadataDocument metadataDocument = ImmutableMetadataDocument.builder()
          .url("https://news.com/n" + i)
          .state(DocumentState.ACTIVE).build();
      db.insertOne(metadataDocument);
    }

    int batches = 0;
    for (List<String> urls : db.getDeletablePages(24, TimeUnit.HOURS, 10)) {
      assertThat(urls.contains("https://news.com/n" + (batches * 10))).isTrue();
      batches++;
    }
    assertThat(batches).isEqualTo(5);
  }

  @Test
  void insertOperation() {
    Operation operation = ImmutableOperation.builder().type(OperationType.CRAWL).state(
        OperationState.IN_PROGRESS).build();
    db.insertOperation(operation);
    String sql = "Select * from " + operationsTable;
    try (Statement stmt = db.getConnection().createStatement()) {
      ResultSet resultSet = stmt.executeQuery(sql);
      assertThat(resultSet.next()).isTrue();
      assertThat(resultSet.getString("type")).isEqualTo(String.valueOf(OperationType.CRAWL));
      assertThat(resultSet.getString("state")).isEqualTo(
          String.valueOf(OperationState.IN_PROGRESS));
      assertThat(UUID.fromString(resultSet.getString("id"))).isEqualTo(operation.id());
      assertThat(resultSet.next()).isFalse();
    } catch (SQLException e) {
      e.printStackTrace();
      assertThat(true).isFalse();
    }
  }

}