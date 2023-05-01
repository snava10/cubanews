package com.snava.cubanews;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;

@Immutable
@JsonSerialize(as = ImmutableClearOldRequestData.class)
@JsonDeserialize(as = ImmutableClearOldRequestData.class)
public interface ClearOldRequestData {
  String indexName();
  @Default
  default int amount() {
    return 3;
  }
  @Default
  default String timeunit() {
    return "DAYS";
  }
}
