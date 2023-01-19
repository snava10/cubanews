package com.snava.cubanews.data.access;

import com.snava.cubanews.ImmutableSqliteMigrationResult;
import com.snava.cubanews.SqliteMigrationResult;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class SqliteMigrationManager {

  // Increasing this number will trigger the migrations
  final int LAST_APPLIED_MIGRATION = 0;
  final int NEWEST_MIGRATION = 1;

  public SqliteMigrationResult runMigrations() throws Exception {
    SqliteMetadataDatabase db = new SqliteMetadataDatabase("/tmp/cubanews/cubanews.db", "na");
    db.initialise();
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    URL url = classLoader.getResource("db_migrations");

    assert url != null;
    int dbVersion = db.getDatabaseVersion();
    Files.list(Path.of(url.getPath())).sorted(Comparator.comparing(Path::getFileName))
        .filter(path -> {
          String filename = path.getFileName().toString();
          int version = Integer.parseInt(filename.split("migration")[1].split("\\.")[0]);
          return version > dbVersion;
        }).forEach(path -> {
          try {
            runMigration(path, db);
          } catch (Exception ex) {
          }
        });

    return ImmutableSqliteMigrationResult.builder().build();
  }

  private boolean runMigration(Path p, SqliteMetadataDatabase db) throws Exception {
    String sql = Files.readString(p);
    db.executeStatement(sql);
    int version = Integer.parseInt(p.getFileName().toString().split("migration")[1].split("\\.")[0]);
    db.setDatabaseVersion(version);
    return true;
  }

  public static void main(String[] args) throws Exception {
    SqliteMigrationManager sqliteMigrationManager = new SqliteMigrationManager();
    sqliteMigrationManager.runMigrations();
  }

}
