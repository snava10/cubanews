package com.snava.crawlandsearch;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

  @Autowired
  Searcher searcher;

  @GetMapping("/api/search/{indexId}")
  public List<IndexDocument> search(@PathVariable long indexId,
      @RequestParam(value = "query") String query) throws IOException, ParseException {
    System.out.println(indexId);
    System.out.println(query);
    Directory index = FSDirectory.open(Paths.get("/tmp/index"));
    return searcher.search(query, index, 100).stream()
        .map(doc -> new IndexDocument(doc.get("url"), doc.get("title"), doc.get("text")))
        .collect(Collectors.toList());
  }

  @GetMapping("/api/search/html/{indexId}")
  public String searchHtml(@PathVariable long indexId, @RequestParam(value = "query") String query)
      throws IOException, ParseException {
    List<IndexDocument> docs = search(indexId, query);
    StringBuilder sb = new StringBuilder("<html><body><div><ul>");
    docs.stream().map(doc -> String.format("<li><a href=\"%s\"><h5>%s</h5></a></li>", doc.getUrl(),
        doc.getTitle())).forEach(sb::append);
    sb.append("</ul></div></body></html>");
    return sb.toString();
  }

}
