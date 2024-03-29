package com.snava.cubanews;

import com.snava.cubanews.data.access.SqliteMetadataDatabase;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.jsoup.Jsoup;

public class HtmlCrawler extends WebCrawler {

  private final static Pattern EXCLUSIONS
      = Pattern.compile(".*(\\.(css|js|xml|gif|jpg|png|mp3|mp4|zip|gz|pdf))$");
  private final Indexer indexer;
  private final Set<String> seeds;
  private final Set<String> baseUrls;
  private final SqliteMetadataDatabase metadataDatabase;
  private final RatedLogger logger;
  private final Operation crawlOperation;

  private final Set<String> tagUrls;

  private final List<IndexDocument> docsToIndex = new ArrayList<>();
  // TODO: Add config for hard coded value.
  // TODO: Evaluate what is the best value for this parameter.
  @SuppressWarnings("FieldCanBeLocal")
  private final int indexBatch = 10;

  public HtmlCrawler(Indexer indexer, Set<String> seeds, Set<String> baseUrls,
      SqliteMetadataDatabase metadataDatabase,
      Operation crawlOperation) {
    super();
    this.indexer = indexer;
    this.seeds = seeds;
    this.baseUrls = baseUrls.stream().map(x -> {
      if (x.endsWith("/")) {
        return x;
      }
      return x + "/";
    }).collect(Collectors.toSet());
    this.metadataDatabase = metadataDatabase;
    this.crawlOperation = crawlOperation;
    this.logger = new RatedLogger(HtmlCrawler.class);
    tagUrls = new HashSet<>();
    tagUrls.addAll(this.baseUrls.stream().map(burl -> burl + "tags/").toList());
    tagUrls.addAll(this.baseUrls.stream().map(burl -> burl + "tag/").toList());
    tagUrls.addAll(this.baseUrls.stream().map(burl -> burl + "etiqueta/").toList());
  }

  @Override
  public boolean shouldVisit(Page referringPage, WebURL url) {
    String urlString = url.getURL().toLowerCase();
    return !EXCLUSIONS.matcher(urlString).matches()
        && matchesAnyBase(url) && !parameterised(url);
  }

  private boolean matchesAnyBase(WebURL url) {
    return baseUrls.stream().anyMatch(url.getURL()::startsWith);
  }

  private boolean parameterised(WebURL url) {
    return url.getURL().contains("?");
  }

  private boolean isTagsPage(WebURL url) {
    return tagUrls.stream().anyMatch(url.getURL()::startsWith);
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
    WebURL webUrl = page.getWebURL();
    String url = page.getWebURL().getURL();
    if (page.getParseData() instanceof HtmlParseData htmlParseData) {
      String title = htmlParseData.getTitle();
      String html = htmlParseData.getHtml();
      String text = html != null ? Jsoup.parse(html).text() : htmlParseData.getText();
      logger.info("{} {}", title, url);
      // do something with the collected data
      IndexDocument doc = ImmutableIndexDocument.builder().url(url).title(title).text(text)
          .lastUpdated(getLastModified(page)).source(getSourceFromDomain(webUrl.getDomain()))
          .build();
      if (metadataDatabase.exists(doc.url())) {
        logger.info("Document with url: {} already exists", doc.url());
      } else if (!isAValidDocument(doc)) {
        logger.info(getValidityDocumentExplanation(doc));
      } else if (Objects.requireNonNull(doc.url()).contains("?")) {
        // Todo: revise this as there may be valid articles being ignored.
        // Possible solution would be to hash the text and compare by similarity to detect valid new articles and avoid duplicates.
        logger.info("Ignoring " + doc.url() + " because contains parameters.");
      } else if (webUrl.getPath().split("/").length < 3) {
        logger.info("Ignoring " + doc.url() + " because is too short to be a final article");
      } else if (isTagsPage(webUrl)) {
        logger.info("Ignoring " + doc.url() + " because is a tags page, not final article");
      } else if (exclusionSet().contains(doc.url())) {
        logger.info("Ignoring " + doc.url() + " because url is in the exclusion set");
      } else if (Arrays.stream(prefixesToIgnore()).anyMatch(prefix -> Objects.requireNonNull(
          doc.url()).startsWith(prefix))) {
        logger.info("Ignoring " + doc.url() + " because contains a prefix manually excluded");
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
              .hash(hashDocBody(doc)).indexName(indexer.getProjectAndIndexName()).build()
      ).collect(Collectors.toList()));
      metadataDatabase.increaseOperationDocCounts(crawlOperation.id(), docsToIndex.size());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    docsToIndex.clear();
  }

  private long getLastModified(Page page) {
    if (page.getFetchResponseHeaders() == null) {
      return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }
    Optional<Header> lastModified = Arrays.stream(page.getFetchResponseHeaders())
        .filter(h -> h.getName().equals("Last-Modified")).findFirst();
    if (lastModified.isEmpty()) {
      return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }
    LocalDateTime date = LocalDateTime.parse(lastModified.get().getValue(),
        DateTimeFormatter.RFC_1123_DATE_TIME);
    return date.toEpochSecond(ZoneOffset.UTC);
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

  private String getSourceFromDomain(String domain) {
    if (domain == null) {
      return "";
    }
    return switch (domain) {
      case "diariodecuba.com" -> "Diario de Cuba";
      case "adncuba.com" -> "ADN Cuba";
      case "14ymedio.com" -> "14Ymedio";
      case "cibercuba.com" -> "Cibercuba";
      case "cubanet.org" -> "Cubanet";
      default -> domain.split("/.")[0];
    };
  }

  private String[] prefixesToIgnore() {
    return new String[] {
        "https://www.cubanet.org/author/",
        "https://www.cubanet.org/categoria/",
        "https://www.cubanet.org/htdocs/"
    };
  }

  private Set<String> exclusionSet() {
    return Stream.of(
        "https://www.cubanet.org/sobre-cubanet/amp/",
        "https://www.cubanet.org/galeria-de-videos/amp/",
        "https://www.cubanet.org/aviso-de-privacidad/amp/"
    ).collect(Collectors.toSet());
  }

}
