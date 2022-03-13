package com.snava.cubanews;

import org.immutables.value.Value.Immutable;

@Immutable
public interface MetadataDocument {
  int id();
  String url();
  long lastUpdated();
  long createdAt();
  DocumentState state();
}
