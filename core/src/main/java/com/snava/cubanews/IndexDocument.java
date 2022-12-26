package com.snava.cubanews;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import javax.annotation.Nullable;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;

@Immutable
@JsonSerialize(as = ImmutableIndexDocument.class)
@JsonDeserialize(as = ImmutableIndexDocument.class)
public interface IndexDocument {
  @Default
  default long lastUpdated() {
    return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
  }
  @Nullable
  String url();
  @Nullable
  String title();
  @Nullable
  String text();
}
