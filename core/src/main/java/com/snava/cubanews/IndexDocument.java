package com.snava.cubanews;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;

@Immutable
public interface IndexDocument {

  @Default
  default long lastUpdated() {
    return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
  }
  String url();
  String title();
  String text();
}
