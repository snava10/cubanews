package com.snava.cubanews;

import com.snava.cubanews.data.access.SqliteMetadataDatabase;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  @GetMapping("/api/metadata/operations")
  public Mono<List<Operation>> getOperations() {
    return Mono.just(db.getOperations());
  }

  @GetMapping("/api/metadata/version")
  public Mono<Integer> getMetadataDBVersion() {
    return Mono.just(db.getDatabaseVersion());
  }

  @GetMapping("/api/metadata/updateState")
  public Mono<Integer> updateAllDocStates(
      @RequestParam(value = "state", defaultValue = "ACTIVE") String state) {
    return Mono.just(db.updateAllStates(DocumentState.valueOf(state)));
  }

  record CountDocumentsResult(int total, int active, int deleted) {

  }
  /**
   * Not implemented.
   * TODO: Implement this method, it should use Firebase to pull the project and the indices in it.
   * @param projectId
   * @return
   */
  public static Set<String> getIndicesForProject(String projectId) {
    return Collections.emptySet();
  }
  @GetMapping("/api/metadata/all")
  public List<MetadataDocument> queryMetadata() throws SQLException {
    return db.getAll();
  }


}
