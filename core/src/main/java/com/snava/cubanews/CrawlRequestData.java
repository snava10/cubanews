package com.snava.cubanews;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Set;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;

@Immutable
@JsonSerialize(as = ImmutableCrawlRequestData.class)
@JsonDeserialize(as = ImmutableCrawlRequestData.class)
public interface CrawlRequestData {

  @Default
  default int limit() {
    return -1;
  }

  @Default
  default int depth() {
    return -1;
  }

  String indexName();

  Set<String> baseUrls();
}
