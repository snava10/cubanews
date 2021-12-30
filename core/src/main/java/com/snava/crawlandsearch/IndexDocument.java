package com.snava.crawlandsearch;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class IndexDocument {

  private final long lastUpdated;
  private final String url;
  private final String title;
  private final String text;

  public IndexDocument(String url, String title, String text) {
    this.lastUpdated = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    this.url = url;
    this.title = title;
    this.text = text;
  }

  public String getUrl() {
    return url;
  }
  public String getTitle() {
    return title;
  }
  public String getText() {
    return text;
  }
  public long getLastUpdated() {
    return lastUpdated;
  }
}
