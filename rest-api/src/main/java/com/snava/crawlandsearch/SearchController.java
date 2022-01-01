package com.snava.crawlandsearch;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class SearchController {

  @Autowired
  Searcher searcher;

  @GetMapping("/api/search/id/{indexId}")
  public Mono<List<IndexDocument>> search(@PathVariable long indexId,
      @RequestParam(value = "query") String query) throws Exception {
    // TODO: Replace println with proper logging
    System.out.println(indexId);
    System.out.println(query);
    Directory index = FSDirectory.open(Paths.get(String.format("/tmp/%s", indexId)));
    // TODO: Add config for hard coded values.
    return Mono.just(searcher.search(query, index, 50).stream()
        .map(doc -> new IndexDocument(doc.get("url"), doc.get("title"), doc.get("text")))
        .collect(Collectors.toList()));
  }

  @GetMapping("/api/search/name/{indexName}")
  public Mono<List<IndexDocument>> searchByName(@PathVariable String indexName,
      @RequestParam(value = "query") String query) throws Exception {
    // TODO: Replace println with proper logging
    System.out.println(indexName);
    System.out.println(query);

    Directory index = FSDirectory.open(Paths.get(String.format("/tmp/%s", indexName)));
    // TODO: Add config for hard coded values.
    return Mono.just(searcher.search(query, index, 50).stream()
        .map(doc -> new IndexDocument(doc.get("url"), doc.get("title"), doc.get("text")))
        .collect(Collectors.toList())).doOnNext(indexDocuments -> {
      try {
        index.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  @GetMapping("/api/search/html/{indexId}")
  public Mono<String> searchHtml(@PathVariable long indexId, @RequestParam(value = "query") String query)
      throws Exception {
    return search(indexId, query).map(docs -> {
      StringBuilder sb = new StringBuilder("<html><body><div><ul>");
      docs.stream().map(doc -> String.format("<li><a href=\"%s\"><h5>%s</h5></a></li>", doc.getUrl(),
          doc.getTitle())).forEach(sb::append);
      sb.append("</ul></div></body></html>");
      return sb.toString();
    });
  }

}
