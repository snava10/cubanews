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

  record CountDocumentsResult(int total, int active, int deleted) {
  }

}
