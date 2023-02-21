package com.snava.cubanews;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import org.immutables.value.Value.Immutable;

@Immutable
@JsonSerialize(as = ImmutableCrawlRequest.class)
@JsonDeserialize(as = ImmutableCrawlRequest.class)
public interface CrawlRequest {
  List<CrawlRequestData> data();
}
