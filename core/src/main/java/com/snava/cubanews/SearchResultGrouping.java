package com.snava.cubanews;

public enum SearchResultGrouping {
  NONE, // All documents from indices are sorted by score
  BY_INDEX_MAX, // Documents are grouped by index. The groups are sorted by the highest scoring document.
}