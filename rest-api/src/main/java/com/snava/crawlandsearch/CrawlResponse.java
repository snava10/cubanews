package com.snava.crawlandsearch;

public class CrawlResponse {
  private final String crawlerId;
  private final CrawlingStatus status;
  public CrawlResponse(String crawlerId) {
    this.crawlerId = crawlerId;
    status = CrawlingStatus.IN_PROGRESS;
  }

  public String getCrawlerId() {
    return crawlerId;
  }

  public CrawlingStatus getStatus() {
    return status;
  }
}

enum CrawlingStatus {
  IN_PROGRESS,
  COMPLETED,
  ERROR
}
