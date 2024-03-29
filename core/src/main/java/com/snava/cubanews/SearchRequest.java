package com.snava.cubanews;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Collections;
import java.util.Set;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;

@Immutable
@JsonSerialize(as = ImmutableSearchRequest.class)
@JsonDeserialize(as = ImmutableSearchRequest.class)
public interface SearchRequest {
  String query();
  @Default
  default Set<String> indices() {
    return Collections.emptySet();
  }
  @Default
  default int maxResultsPerIndex() {
    return 5;
  }
  @Default
  default int maxTotalResults() {
    return 50;
  }

  @Default
  default SearchResultGrouping grouping() {
    return SearchResultGrouping.NONE;
  }

}
