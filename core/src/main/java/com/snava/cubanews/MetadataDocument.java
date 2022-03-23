package com.snava.cubanews;

import javax.annotation.Nullable;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;

@Immutable
public interface MetadataDocument {
  @Nullable
  Integer id();
  String url();
  @Default
  default long lastUpdated() {
    return 0;
  }
  @Default
  default long createdAt() {
    return 0;
  }
  @Default
  default DocumentState state() {
    return DocumentState.ACTIVE;
  };
  @Nullable
  String hash();
}
