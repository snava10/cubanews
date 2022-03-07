package com.snava.cubanews;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class MetadataController {

  @Autowired
  Firestore db;

  @Autowired
  String countersCollectionName;

  @GetMapping("/api/metadata/countDocuments")
  public Mono<Long> countDocuments() throws ExecutionException, InterruptedException {
    DocumentReference docRef = db.collection(countersCollectionName)
        .document("pages-total-counter");
    ApiFuture<DocumentSnapshot> future = docRef.get();
    DocumentSnapshot document = future.get();
    if (document.exists()) {
      return Mono.just(Objects.requireNonNull(document.getLong("totalDocs")));
    } else {
      System.out.println("No such document!");
      return Mono.just(0L);
    }
  }

}
