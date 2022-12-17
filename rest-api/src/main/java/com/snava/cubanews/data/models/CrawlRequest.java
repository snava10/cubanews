package com.snava.cubanews.data.models;

import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CrawlRequest {
  private final Optional<Integer> limit;
  private final Set<String> baseUrls;
  private final String indexName;

  public CrawlRequest(@JsonProperty("indexName") String indexName,
      @JsonProperty("baseUrls") Set<String> baseUrls, @JsonProperty("limit") Optional<Integer> limit) {
    this.baseUrls = baseUrls;
    this.indexName = indexName;
    this.limit = limit.isEmpty() ? Optional.of(Integer.MAX_VALUE) : limit;
  }

  public int getLimit() {
    return limit.orElseThrow();
  }

  public Set<String> getBaseUrls() {
    return baseUrls;
  }

  public String getIndexName() {
    return indexName;
  }

  @Override
  public String toString() {
    return String.format("indexName: %s, limit: %d, baseUrls: [%s]",
        getIndexName(), getLimit(), String.join(",", baseUrls));
  }
}
