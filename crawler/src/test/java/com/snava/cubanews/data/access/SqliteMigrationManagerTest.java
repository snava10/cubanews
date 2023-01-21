package com.snava.cubanews.data.access;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SqliteMigrationManagerTest {

  SqliteMetadataDatabase db;


  @BeforeEach
  void setUp() throws SQLException {
    db = new SqliteMetadataDatabase("cubanews-test.db", "pages");
    db.initialise();

  }

  @AfterEach
  void tearDown() throws SQLException, IOException {
    db.close();
    FileUtils.forceDelete(FileUtils.getFile("cubanews-test.db"));
    FileUtils.forceDelete(FileUtils.getFile("cubanews-test.db_back"));
  }

  @Test
  void runMigrations() throws Exception {
    assertThat(db.getDatabaseVersion()).isEqualTo(0);
    SqliteMigrationManager sqliteMigrationManager = new SqliteMigrationManager(db);
    sqliteMigrationManager.runMigrations();
    assertThat(db.getDatabaseVersion()).isEqualTo(2);
    String sql = "PRAGMA table_info(pages);";
    try (Statement stmt = db.conn.createStatement()) {
      ResultSet resultSet = stmt.executeQuery(sql);
      Set<String> columnNames = new HashSet<>();
      while (resultSet.next()) {
        columnNames.add(resultSet.getString("name"));
      }
      assertThat(columnNames).containsAll(Arrays.asList("hash", "imageURI", "other"));
    }
  }
}