package com.snava.cubanews.data.access;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SqliteMetadataDatabaseTest {

  static SqliteMetadataDatabase metadataDatabase;
  static String dbPath;
  static Random r = new Random();
  static String metaTable = "metaTable";

  @BeforeAll
  static void setUp() throws SQLException {
    Path currentRelativePath = Paths.get("");
    String s = currentRelativePath.toAbsolutePath().toString();
    System.out.println("Current absolute path is: " + s);
    dbPath = s + "test" + r.nextInt(100000000) + ".db";
    metadataDatabase = new SqliteMetadataDatabase(dbPath, metaTable);
    metadataDatabase.initialise();
  }

  @AfterAll
  static void tearDown() throws SQLException {
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
        "SELECT name FROM sqlite_master WHERE type='table' AND name='%s'", metaTable);
    Statement stmt = metadataDatabase.getConnection().createStatement();
    stmt.execute(tableExistsSQL);
    ResultSet resultSet = stmt.getResultSet();
    resultSet.next();
    assertThat(resultSet.getString("name")).isEqualTo(metaTable);
    stmt.close();
  }

  @Test
  void getByUrlAndStateTest() {

  }

}