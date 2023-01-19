package com.snava.cubanews;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Collections;
import java.util.List;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;

@Immutable
@JsonSerialize(as = ImmutableSqliteMigrationResult.class)
@JsonDeserialize(as = ImmutableSqliteMigrationResult.class)
public interface SqliteMigrationResult {
  @Default
  default List<String> succeededMigrations() {
    return Collections.emptyList();
  }
  @Default
  default List<String> failedMigrations() {
    return Collections.emptyList();
  }
  @Default
  default List<String> errors() {
    return Collections.emptyList();
  }
}
