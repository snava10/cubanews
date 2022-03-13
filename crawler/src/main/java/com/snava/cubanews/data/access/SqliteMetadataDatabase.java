package com.snava.cubanews.data.access;

import com.snava.cubanews.DocumentState;
import com.snava.cubanews.ImmutableMetadataDocument;
import com.snava.cubanews.MetadataDocument;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

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
          + " createdAt NUMERIC NOT NULL);", tableName);
    try (Statement stmt = conn.createStatement()) {
      stmt.execute(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public MetadataDocument getByUrlAndState(String url, DocumentState state) throws SQLException {
    String sql = "select * from " + tableName + " where url=? and state=?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, url);
      stmt.setString(2, state.toString());
      ResultSet resultSet = stmt.executeQuery();
      if (resultSet.next()) {
        return ImmutableMetadataDocument.builder()
            .url(resultSet.getString("url"))
            .createdAt(resultSet.getLong("createdAt")).lastUpdated(resultSet.getLong("lastUpdated"))
            .state(resultSet.getObject("state", DocumentState.class))
            .build();
      }
      return null;
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
      throw ex;
    }
  }

  public void insertOne(MetadataDocument metadataDocument) throws SQLException {
    String sql = "INSERT INTO " + tableName + "(url,lastUpdated,createdAt,state) VALUES(?,?,?,?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, metadataDocument.url());
      stmt.setLong(2, metadataDocument.lastUpdated());
      stmt.setLong(3, metadataDocument.createdAt());
      stmt.setObject(4, metadataDocument.state());
      stmt.execute();
    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    }
  }

  public void insertMany(List<MetadataDocument> metadataDocumentList) {
  }

  /**
   * Update the state of a document by url. It updates the lastUpdated field as well.
   * @param url Url of the document. This should be unique.
   * @param documentState The state to set the document to.
   */
  public void updateState(String url, DocumentState documentState) {

  }

}
