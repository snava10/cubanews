package com.snava.cubanews;

import com.snava.cubanews.Searcher.SearcherResult;
import com.snava.cubanews.data.access.SqliteMetadataDatabase;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class SearchController {

  @Autowired
  Searcher searcher;

  @Autowired
  String homePath;

  @Autowired
  SqliteMetadataDatabase db;

  RatedLogger logger = new RatedLogger(SearchController.class, 0, 1, 1);

  @GetMapping("/api/search/id/{indexId}")
  public Mono<List<IndexDocument>> search(@PathVariable long indexId,
      @RequestParam(value = "query") String query) throws Exception {
    logger.info("{}", indexId);
    logger.info(query);
    Directory index = FSDirectory.open(Paths.get(String.format("/tmp/%s", indexId)));
    // TODO: Add config for hard coded values.
    return Mono.just(search(query, index, 50));
  }

  @GetMapping("/api/search/name/{indexName}")
  public Mono<List<IndexDocument>> searchByName(@PathVariable String indexName,
      @RequestParam(value = "query") String query) throws Exception {
    logger.info(indexName);
    logger.info(query);

    Directory index = FSDirectory.open(Paths.get(homePath + indexName));
    // TODO: Add config for hard coded values.
    return Mono.just(search(query, index, 50)).doOnNext(indexDocuments -> {
      try {
        index.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  @PostMapping("/api/search/project/{projectId}")
  public Mono<List<IndexDocument>> searchByProject(@PathVariable String projectId,
      @RequestBody SearchRequest searchRequest) {
    Set<String> indices = searchRequest.indices().isEmpty() ? getIndicesForProject(projectId)
        : searchRequest.indices();

    return Mono.just(indices.parallelStream().flatMap(indexName -> {
      try {
        Directory index = FSDirectory.open(Paths.get(homePath + indexName));
        return search(searchRequest.query(), index, 50).stream();
      } catch (IOException e) {
        logger.error(String.format("Error searching on index %s", indexName), e);
        return Stream.empty();
      }
    }).sorted(Comparator.comparing(IndexDocument::score).reversed()).collect(Collectors.toList()));

  }

  @GetMapping("/api/search/html/{indexId}")
  public Mono<String> searchHtml(@PathVariable long indexId,
      @RequestParam(value = "query") String query) throws Exception {
    return search(indexId, query).map(docs -> {
      StringBuilder sb = new StringBuilder("<html><body><div><ul>");
      docs.stream().map(
          doc -> String.format("<li><a href=\"%s\"><h5>%s</h5></a></li>", doc.url(),
              doc.title())).forEach(sb::append);
      sb.append("</ul></div></body></html>");
      return sb.toString();
    });
  }

  private List<IndexDocument> search(String query, Directory index, int limit) throws IOException {
    logger.info("Searching {} on index {}", query, index.toString());
    List<SearcherResult> searcherResults = searcher.search(query, index, limit);
    if (searcherResults.isEmpty()) {
      return Collections.singletonList(ImmutableIndexDocument.builder()
          .title("No results :(").build());
    }
    return searcher.search(query, index, limit).stream().map(
        searcherResult -> {
          Document doc = searcherResult.doc();
          long lastUpdated = doc.get("lastUpdatedNumericStored") == null ? 0
              : Long.parseLong(doc.get("lastUpdatedNumericStored"));

          if (lastUpdated == 0) {
            Optional<MetadataDocument> metadataDocument = db.getByUrl(doc.get("url"));
            if (metadataDocument.isPresent()) {
              lastUpdated = metadataDocument.get().lastUpdated();
            }
          }
          return ImmutableIndexDocument.builder().url(doc.get("url"))
              .title(doc.get("title"))
              .text(doc.get("text"))
              .lastUpdated(lastUpdated)
              .lastUpdatedDisplay(doc.get("lastUpdated"))
              .score(searcherResult.score())
              .build();
        }
    ).collect(Collectors.toList());
  }

  private Set<String> getIndicesForProject(String projectId) {
    return Collections.emptySet();
  }

}
