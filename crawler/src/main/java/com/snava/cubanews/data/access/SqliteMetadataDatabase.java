package com.snava.cubanews.data.access;

import com.google.cloud.Tuple;
import com.snava.cubanews.DocumentState;
import com.snava.cubanews.ImmutableMetadataDocument;
import com.snava.cubanews.MetadataDocument;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class SqliteMetadataDatabase {

  String url = "jdbc:sqlite:";
  String dbPath;
  Connection conn;
  String tableName;

  public SqliteMetadataDatabase(String dbPath, String tableName) {
    this.dbPath = dbPath;
    url += dbPath;
    this.tableName = tableName;
  }

  public String getUrl() {
    return url;
  }

  public String getDbPath() {
    return dbPath;
  }

  public Connection getConnection() {
    return conn;
  }

  /**
   * Opens the database connection. Creates the database and metadata table if it doesn't exists.
   * @throws SQLException
   */
  public void initialise() throws SQLException {
    connect();
    createMetadataTable();
  }

  public void close() throws SQLException {
    conn.close();
  }

  private void connect() throws SQLException {
    conn = DriverManager.getConnection(url);
  }

  private void createMetadataTable() {
    String sql = String.format("CREATE TABLE IF NOT EXISTS %s (\n"
        + "	id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n"
        + "	url TEXT NOT NULL UNIQUE,\n"
        + "	lastUpdated NUMERIC NOT NULL,\n"
        + " createdAt NUMERIC NOT NULL, \n"
        + " state TEXT NOT NULL);", tableName);
    String sqlIndex = String.format("CREATE UNIQUE INDEX IF NOT EXISTS idx_url ON %s (url);",
        tableName);
    try (Statement stmt = conn.createStatement()) {
      stmt.execute(sql);
      stmt.execute(sqlIndex);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public MetadataDocument getByUrlAndState(String url, DocumentState state) {
    String sql = "select id, url, createdAt, lastUpdated, state from " + tableName + " where url=? and state=?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, url);
      stmt.setString(2, state.name());
      ResultSet resultSet = stmt.executeQuery();
      if (resultSet.next()) {
        return ImmutableMetadataDocument.builder()
            .id(resultSet.getInt("id"))
            .url(resultSet.getString("url"))
            .createdAt(resultSet.getLong("createdAt")).lastUpdated(resultSet.getLong("lastUpdated"))
            .state(DocumentState.valueOf(resultSet.getString("state")))
            .build();
      }
      return null;
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
      throw new RuntimeException(ex);
    }
  }

  public Optional<MetadataDocument> getByUrl(String url) {
    String sql = "select id, url, createdAt, lastUpdated, state from " + tableName + " where url=?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, url);
      ResultSet resultSet = stmt.executeQuery();
      if (resultSet.next()) {
        return Optional.of(ImmutableMetadataDocument.builder()
            .id(resultSet.getInt("id"))
            .url(resultSet.getString("url"))
            .createdAt(resultSet.getLong("createdAt"))
            .lastUpdated(resultSet.getLong("lastUpdated"))
            .state(DocumentState.valueOf(resultSet.getString("state")))
            .build());
      }
      return Optional.empty();
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
      throw new RuntimeException(ex);
    }
  }

  public boolean exists(String url) {
    return getByUrl(url).isPresent();
  }

  public int count() {
    String sql = "Select count(id) as c from " + tableName;
    try (Statement stmt = conn.createStatement()) {
      ResultSet resultSet = stmt.executeQuery(sql);
      resultSet.next();
      return resultSet.getInt("c");
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public int count(DocumentState documentState) {
    String sql = "Select count(id) as c from " + tableName + " where state=?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, documentState.name());
      ResultSet resultSet = stmt.executeQuery();
      resultSet.next();
      return resultSet.getInt("c");
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public void insertOne(MetadataDocument metadataDocument) {
    String sql = "INSERT INTO " + tableName + "(url,lastUpdated,createdAt,state) VALUES(?,?,?,?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
      stmt.setString(1, metadataDocument.url());
      stmt.setLong(2, timestamp);
      stmt.setLong(3, timestamp);
      stmt.setObject(4, metadataDocument.state());
      stmt.execute();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public void insertMany(List<MetadataDocument> metadataDocumentList) {
    String sql = "INSERT INTO " + tableName + "(url,lastUpdated,createdAt,state) VALUES(?,?,?,?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
      for (MetadataDocument metadataDocument : metadataDocumentList) {
        stmt.setString(1, metadataDocument.url());
        stmt.setLong(2, timestamp);
        stmt.setLong(3, timestamp);
        stmt.setObject(4, metadataDocument.state());
        stmt.addBatch();
      }
      int[] result = stmt.executeBatch();
      System.out.println("The number of rows inserted: " + result.length);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  /**
   * Update the state of a document by url. It updates the lastUpdated field as well.
   *
   * @param url           Url of the document. This should be unique.
   * @param documentState The state to set the document to.
   */
  public void updateState(String url, DocumentState documentState) {
    String sql = "UPDATE " + tableName + " SET state=?, lastUpdated=? WHERE url=?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, documentState.name());
      stmt.setLong(2, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
      stmt.setString(3, url);
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  /**
   * Update the state of a set of documents.
   *
   * @param urlStates A list containing pairs of url and document state.
   */
  public void updateManyStates(List<Tuple<String, DocumentState>> urlStates) {
    String sql = "UPDATE " + tableName + " SET state=?, lastUpdated=? WHERE url=?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      for (Tuple<String, DocumentState> tuple : urlStates) {
        stmt.setString(1, tuple.y().name());
        stmt.setLong(2, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        stmt.setString(3, tuple.x());
        stmt.addBatch();
      }
      int[] result = stmt.executeBatch();
      System.out.println("The number of rows updated: " + result.length);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public int updateStateByAgeAndState(int amount, TimeUnit timeUnit, DocumentState current,
      DocumentState state) {
    long seconds = timeUnit.toSeconds(amount);
    long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    long delta = timestamp - seconds;
    String sql = "Update " + tableName + " SET state=? where createdAt < ? and state=?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, state.name());
      stmt.setLong(2, delta);
      stmt.setString(3, current.name());
      return stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public Iterable<List<String>> getDeletablePages(int amount, TimeUnit timeUnit, int batchSize) {
    long seconds = timeUnit.toSeconds(amount);
    long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    long delta = timestamp - seconds;
    ResultSet resultSet;
    String sql = "Select url from " + tableName + " where state='ACTIVE' and createdAt < ?";
    PreparedStatement stmt;
    try {
      stmt = conn.prepareStatement(sql);
      stmt.setLong(1, delta);
      resultSet = stmt.executeQuery();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    return () -> new UrlsIterator(stmt, resultSet, batchSize);
  }

  static class UrlsIterator implements Iterator<List<String>> {

    private final PreparedStatement stmt;
    private final ResultSet resultSet;
    private final int batchSize;
    List<String> batch = new ArrayList<>();

    public UrlsIterator(PreparedStatement stmt, ResultSet resultSet, int batchSize) {
      this.stmt = stmt;
      this.resultSet = resultSet;
      this.batchSize = batchSize;
    }

    @Override
    public boolean hasNext() {
      batch = new ArrayList<>();
      try {
        while (resultSet.next()) {
          batch.add(resultSet.getString("url"));
          if (batch.size() == batchSize) {
            break;
          }
        }
        if (batch.isEmpty()) {
          resultSet.close();
          stmt.close();
        }
        return !batch.isEmpty();
      } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      }
    }

    @Override
    public List<String> next() {
      return batch;
    }
  }
}
