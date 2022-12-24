package com.snava.cubanews.data.models;

public record LongRunningOperationResponse(String index,
                                           com.snava.cubanews.data.models.LongRunningOperationResponse.OperationStatus status,
                                           com.snava.cubanews.data.models.LongRunningOperationResponse.OperationType type) {


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
