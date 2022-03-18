package com.snava.cubanews;

import com.snava.cubanews.data.access.SqliteMetadataDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class MetadataController {

  @Autowired
  SqliteMetadataDatabase db;

  @GetMapping("/api/metadata/countDocuments")
  public Mono<CountDocumentsResult> countDocuments() {
    int total = db.count();
    int active = db.count(DocumentState.ACTIVE);
    int deleted = db.count(DocumentState.DELETED);
    return Mono.just(new CountDocumentsResult(total, active, deleted));
  }

  static class CountDocumentsResult {
    private final int total;
    private final int active;
    private final int deleted;

    CountDocumentsResult(int total, int active, int deleted) {
      this.total = total;
      this.active = active;
      this.deleted = deleted;
    }

    public int getTotal() {
      return total;
    }

    public int getActive() {
      return active;
    }

    public int getDeleted() {
      return deleted;
    }
  }

}
