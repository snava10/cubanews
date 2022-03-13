package com.snava.cubanews;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.WriteResult;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class HtmlCrawler extends WebCrawler {

  private final static Pattern EXCLUSIONS
      = Pattern.compile(".*(\\.(css|js|xml|gif|jpg|png|mp3|mp4|zip|gz|pdf))$");
  private final Indexer indexer;
  private final Set<String> seeds;
  private Firestore db;

  private final List<IndexDocument> docsToIndex = new ArrayList<>();
  // TODO: Add config for hard coded value.
  // TODO: Evaluate what is the best value for this parameter.
  private final int indexBatch = 10;

  public HtmlCrawler(Indexer indexer, Set<String> seeds) {
    super();
    this.indexer = indexer;
    this.seeds = seeds;
  }

  @Override
  public boolean shouldVisit(Page referringPage, WebURL url) {
    String urlString = url.getURL().toLowerCase();
    return !EXCLUSIONS.matcher(urlString).matches()
        && matchesAnySeed(url);
  }

  private boolean matchesAnySeed(WebURL url) {
    return seeds.stream().anyMatch(url.getURL()::startsWith);
  }

  @Override
  public void onStart() {
    flushBuffer();
  }

  @Override
  public void onBeforeExit() {
    flushBuffer();
  }

  @Override
  public void visit(Page page) {
    String url = page.getWebURL().getURL();

    if (page.getParseData() instanceof HtmlParseData) {
      HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
      String title = htmlParseData.getTitle();
      String text = htmlParseData.getText();
      String html = htmlParseData.getHtml();
      Set<WebURL> links = htmlParseData.getOutgoingUrls();
      System.out.printf("%s %s%n", title, url);
      // do something with the collected data
      IndexDocument doc = ImmutableIndexDocument.builder().url(url).title(title).text(text).build();
      if (isAValidDocument(doc)) {
        docsToIndex.add(doc);
      } else {
        // Todo: Use a logger
        System.out.println(getValidityDocumentExplanation(doc));
      }
      if (docsToIndex.size() >= indexBatch) {
        flushBuffer();
      }
    }
  }

  List<IndexDocument> getDocsToIndex() {
    return docsToIndex;
  }

  private void flushBuffer() {
    try {
      indexer.index(docsToIndex);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    docsToIndex.clear();
  }

  private String getValidityDocumentExplanation(IndexDocument indexDocument) {
    String OK = "Ok";
    String NULL = "Null";
    return String.format("url: %s, title: %s, text: %s",
        StringUtils.isBlank(indexDocument.url()) ? NULL : OK,
        StringUtils.isBlank(indexDocument.title()) ? NULL : OK,
        StringUtils.isBlank(indexDocument.text()) ? NULL : OK
    );
  }

  private boolean isAValidDocument(IndexDocument indexDocument) {
    return StringUtils.isNotBlank(indexDocument.url()) && StringUtils.isNotBlank(
        indexDocument.title())
        && StringUtils.isNotBlank(indexDocument.text());
  }

}
