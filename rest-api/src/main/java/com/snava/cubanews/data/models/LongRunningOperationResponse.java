package com.snava.cubanews.data.models;

public record LongRunningOperationResponse(String index,
                                           OperationStatus status,
                                           OperationType type) {

  public enum OperationStatus {
    IN_PROGRESS,
    COMPLETED,
    ERROR
  }

  public enum OperationType {
    CRAWL,
    DELETE_OLD_PAGES
  }

}
