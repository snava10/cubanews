package com.snava.cubanews;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import javax.annotation.Nullable;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;

@Immutable
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
