package com.snava.cubanews;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javax.annotation.Nullable;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;

@Immutable
@JsonSerialize(as = ImmutableMetadataDocument.class)
@JsonDeserialize(as = ImmutableMetadataDocument.class)
public interface MetadataDocument {

  @Nullable
  Integer id();

  String url();

  @Nullable
  String hash();

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
  }
}
