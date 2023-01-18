package com.snava.cubanews;

import com.snava.cubanews.data.access.SqliteMetadataDatabase;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.jsoup.Jsoup;

public class HtmlCrawler extends WebCrawler {

  private final static Pattern EXCLUSIONS
      = Pattern.compile(".*(\\.(css|js|xml|gif|jpg|png|mp3|mp4|zip|gz|pdf))$");
  private final Indexer indexer;
  private final Set<String> seeds;
  private final SqliteMetadataDatabase metadataDatabase;

  private final Operation crawlOperation;

  private final List<IndexDocument> docsToIndex = new ArrayList<>();
  // TODO: Add config for hard coded value.
  // TODO: Evaluate what is the best value for this parameter.
  private final int indexBatch = 10;

  public HtmlCrawler(Indexer indexer, Set<String> seeds, SqliteMetadataDatabase metadataDatabase,
      Operation crawlOperation) {
    super();
    this.indexer = indexer;
    this.seeds = seeds;
    this.metadataDatabase = metadataDatabase;
    this.crawlOperation = crawlOperation;
  }

  @Override
  public boolean shouldVisit(Page referringPage, WebURL url) {
    String urlString = url.getURL().toLowerCase();
    return !EXCLUSIONS.matcher(urlString).matches()
        && matchesAnySeed(url) && !parameterised(url);
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
    if (page.getParseData() instanceof HtmlParseData htmlParseData) {
      String title = htmlParseData.getTitle();
      String html = htmlParseData.getHtml();
      String text = html != null ? Jsoup.parse(html).text() : htmlParseData.getText();
      System.out.printf("%s %s%n", title, url);
      // do something with the collected data
      IndexDocument doc = ImmutableIndexDocument.builder().url(url).title(title).text(text)
          .lastUpdated(getLastModified(page)).build();
      if (metadataDatabase.exists(doc.url())) {
        // Todo: Use a logger
        System.out.printf("Document with url: %s already exists%n", doc.url());
      } else if (!isAValidDocument(doc)) {
        // Todo: Use a logger
        System.out.println(getValidityDocumentExplanation(doc));
      } else if (Objects.requireNonNull(doc.url()).contains("?")) {
        // Todo: revise this as there may be valid articles being ignored.
        // Possible solution would be to hash the text and compare by similarity to detect valid new articles and avoid duplicates.
        System.out.println("Ignoring " + doc.url() + " because contains parameters.");
      } else {
        docsToIndex.add(doc);
      }
      if (docsToIndex.size() >= indexBatch) {
        flushBuffer();
      }
    }
  }

  List<IndexDocument> getDocsToIndex() {
    return docsToIndex;
  }

  private String hashDocBody(IndexDocument doc) {
    return DigestUtils.sha256Hex(doc.text());
  }

  private void flushBuffer() {
    try {
      indexer.index(docsToIndex);
      metadataDatabase.insertMany(docsToIndex.stream().map(
          doc -> ImmutableMetadataDocument.builder().url(Objects.requireNonNull(doc.url()))
              .hash(hashDocBody(doc)).build()
      ).collect(Collectors.toList()));
      metadataDatabase.increaseOperationDocCounts(crawlOperation.id(), docsToIndex.size());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    docsToIndex.clear();
  }

  private long getLastModified(Page page) {
    Optional<Header> lastModified = Arrays.stream(page.getFetchResponseHeaders())
        .filter(h -> h.getName().equals("Last-Modified")).findFirst();
    if (lastModified.isEmpty()) {
      return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }
    LocalDate date = LocalDate.parse(lastModified.get().getValue(),
        DateTimeFormatter.RFC_1123_DATE_TIME);
    return date.toEpochSecond(LocalTime.now(), ZoneOffset.UTC);
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
