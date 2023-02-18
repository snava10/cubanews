package com.snava.cubanews;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Collections;
import java.util.Set;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;

@Immutable
@JsonSerialize(as = ImmutableCrawlRequest.class)
@JsonDeserialize(as = ImmutableCrawlRequest.class)
public interface CrawlRequest {
  @Default
  default int limit() {
    return Integer.MAX_VALUE;
  }
  @Default
  default Set<String> indices() {
    return Collections.emptySet();
  };
  Set<String> baseUrls();
}
