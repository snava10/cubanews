package com.snava.cubanews;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class IndexDocumentTest {

  @Test
  public void createIndexDocument_shouldSetLastUpdated() {
    IndexDocument doc = ImmutableIndexDocument.builder()
        .url("url").title("title").text("text").build();
    assertEquals(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC), doc.lastUpdated());
  }

}