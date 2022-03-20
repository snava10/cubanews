package com.snava.cubanews.data.models;

public class LongRunningOperationResponse {

  private final String index;
  private final OperationStatus status;
  private final OperationType type;

  public LongRunningOperationResponse(String index,
      OperationStatus status,
      OperationType type) {
    this.index = index;
    this.status = status;
    this.type = type;
  }

  public String getIndex() {
    return index;
  }

  public OperationStatus getStatus() {
    return status;
  }

  public OperationType getType() {
    return type;
  }


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
