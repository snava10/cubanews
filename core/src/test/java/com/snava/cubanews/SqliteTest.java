package com.snava.cubanews;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

public class SqliteTest {

  @Test
  public void createDatabase() {
    String file = "/tmp/sqlite/test.db";
    String url = "jdbc:sqlite:" + file;
    try (Connection conn = DriverManager.getConnection(url)) {
      if (conn != null) {
        DatabaseMetaData meta = conn.getMetaData();
        System.out.println("The driver name is " + meta.getDriverName());
        System.out.println("A new database has been created.");
      }

      File f = new File(file);
      assertThat(f.exists()).isTrue();
      assertThat(f.delete()).isTrue();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
}
