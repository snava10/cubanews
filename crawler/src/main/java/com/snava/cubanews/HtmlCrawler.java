package com.snava.cubanews;

import com.snava.cubanews.data.access.SqliteMetadataDatabase;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class HtmlCrawler extends WebCrawler {

  private final static Pattern EXCLUSIONS
      = Pattern.compile(".*(\\.(css|js|xml|gif|jpg|png|mp3|mp4|zip|gz|pdf))$");
  private final Indexer indexer;
  private final Set<String> seeds;
  private final SqliteMetadataDatabase metadataDatabase;
  private final PagesHashing pagesHashing;

  private final List<IndexDocument> docsToIndex = new ArrayList<>();
  // TODO: Add config for hard coded value.
  // TODO: Evaluate what is the best value for this parameter.
  private final int INDEX_BATCH = 100;

  public HtmlCrawler(Indexer indexer, Set<String> seeds, SqliteMetadataDatabase metadataDatabase,
      PagesHashing pagesHashing) {
    super();
    this.indexer = indexer;
    this.seeds = seeds;
    this.metadataDatabase = metadataDatabase;
    this.pagesHashing = pagesHashing;
  }

  @Override
  public boolean shouldVisit(Page referringPage, WebURL url) {
    String urlString = url.getURL().toLowerCase();
    if (!EXCLUSIONS.matcher(urlString).matches()
        && matchesAnySeed(url) && !parameterised(url)) {
      return true;
    }
    return pagesHashing.pageExists(referringPage);
  }

  private boolean matchesAnySeed(WebURL url) {
    return seeds.stream().anyMatch(url.getURL()::startsWith);
  }

  private boolean parameterised(WebURL url) {
    return url.getURL().contains("?");
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
//      Set<WebURL> links = htmlParseData.getOutgoingUrls();
      System.out.printf("%s %s%n", title, url);
      // do something with the collected data
      IndexDocument doc = ImmutableIndexDocument.builder().url(url).title(title).text(text)
          .hash(pagesHashing.getHash(page))
          .build();
      if (metadataDatabase.exists(doc.url())) {
        // Todo: Use a logger
        System.out.printf("Document with url: %s already exists%n", doc.url());
      } else if (!isAValidDocument(doc)) {
        // Todo: Use a logger
        System.out.println(getValidityDocumentExplanation(doc));
      } else if (doc.url().contains("?")) {
        System.out.println("Ignoring " + doc.url() + " because contains parameters.");
      } else {
        docsToIndex.add(doc);
      }
      if (docsToIndex.size() >= INDEX_BATCH) {
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
      metadataDatabase.insertMany(docsToIndex.stream().map(
          doc -> ImmutableMetadataDocument.builder().url(Objects.requireNonNull(doc.url()))
              .hash(doc.hash()).build()
      ).collect(Collectors.toList()));
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
