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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SqliteMetadataDatabaseTest {

  static SqliteMetadataDatabase metadataDatabase;
  static String dbPath;
  static Random r = new Random();
  static String metaTable = "metaTable";

  @BeforeEach
  void setUp() throws SQLException {
    Path currentRelativePath = Paths.get("");
    String s = currentRelativePath.toAbsolutePath().toString();
    System.out.println("Current absolute path is: " + s);
    dbPath = s + "test" + r.nextInt(100000000) + ".db";
    metadataDatabase = new SqliteMetadataDatabase(dbPath, metaTable);
    metadataDatabase.initialise();
  }

  @AfterEach
  void tearDown() throws SQLException {
    File f = new File(dbPath);
    metadataDatabase.getConnection().close();
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
    assertThat(metadataDatabase.getConnection()).isNotNull();
  }

  @Test
  void createMetadataTable() throws SQLException {
    String tableExistsSQL = String.format(
        "SELECT name FROM sqlite_master WHERE type='table' AND name='%s'", metaTable
    );
    String indexExistSQL = "SELECT name FROM sqlite_master WHERE type='index' AND name='idx_url'";
    Statement stmt = metadataDatabase.getConnection().createStatement();

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
    metadataDatabase.insertMany(Arrays.asList(activeDoc, deletedDoc));

    MetadataDocument activeDocReal = metadataDatabase.getByUrlAndState("url1",
        DocumentState.ACTIVE);
    activeDoc = ImmutableMetadataDocument.copyOf(activeDoc).withId(activeDocReal.id());
    assertThat(activeDocReal).isEqualTo(activeDoc);

    MetadataDocument deletedDocReal = metadataDatabase.getByUrlAndState("url2",
        DocumentState.DELETED);
    deletedDoc = ImmutableMetadataDocument.copyOf(deletedDoc).withId(deletedDocReal.id());
    assertThat(deletedDocReal).isEqualTo(deletedDoc);

    assertThat(metadataDatabase.getByUrlAndState("url1", DocumentState.DELETED)).isNull();
    assertThat(metadataDatabase.getByUrlAndState("urlx", DocumentState.ACTIVE)).isNull();
  }

  @Test
  void insertOne() {
    long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    MetadataDocument metadataDocument = ImmutableMetadataDocument.builder()
        .url("https://news.com/n1").state(DocumentState.ACTIVE).build();
    metadataDatabase.insertOne(metadataDocument);

    String sql = "Select * from " + metaTable;
    try (Statement stmt = metadataDatabase.getConnection().createStatement()) {
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

    metadataDatabase.insertMany(Arrays.asList(metadataDocument, metadataDocument2));

    String sql = "SELECT * FROM " + metaTable;
    try (Statement stmt = metadataDatabase.getConnection().createStatement()) {
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
    metadataDatabase.insertOne(metadataDocument);
    Thread.sleep(1000);
    metadataDatabase.updateState(metadataDocument.url(), DocumentState.DELETED);
    MetadataDocument md = metadataDatabase.getByUrlAndState(metadataDocument.url(),
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

    metadataDatabase.insertMany(Arrays.asList(metadataDocument, metadataDocument2));
    metadataDatabase.updateManyStates(list);
    assertThat(metadataDatabase.getByUrlAndState(metadataDocument.url(),
        DocumentState.DELETED)).isNotNull();
    assertThat(metadataDatabase.getByUrlAndState(metadataDocument.url(),
        DocumentState.DELETED).lastUpdated()).isGreaterThan(timestamp);
    assertThat(metadataDatabase.getByUrlAndState(metadataDocument2.url(),
        DocumentState.DELETED)).isNotNull();
    assertThat(metadataDatabase.getByUrlAndState(metadataDocument2.url(),
        DocumentState.DELETED).lastUpdated()).isGreaterThan(timestamp);
  }

}