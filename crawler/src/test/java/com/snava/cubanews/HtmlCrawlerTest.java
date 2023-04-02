package com.snava.cubanews;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.snava.cubanews.data.access.SqliteMetadataDatabase;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HtmlCrawlerTest {

  HtmlCrawler crawler;
  Indexer indexer;
  SqliteMetadataDatabase metadataDatabase;

  @BeforeEach
  public void setup() {
    indexer = mock(Indexer.class);
    metadataDatabase = mock(SqliteMetadataDatabase.class);
    Operation op = ImmutableOperation.builder().type(OperationType.CRAWL)
        .state(OperationState.IN_PROGRESS).build();
    Set<String> seeds = Stream.of("https://url1.com/", "https://url2.com/")
        .collect(Collectors.toSet());
    crawler = new HtmlCrawler(indexer, seeds, seeds, metadataDatabase, op);
  }

  @Test
  void visit() {
  }

  @ParameterizedTest
  @CsvSource({",,", "  ,  ,  ", "http://ad.com?a=1,goodTitle,goodText"})
  void visit_InvalidDocument(String url, String title, String text) {
    when(metadataDatabase.exists(anyString())).thenReturn(false);
    Page pageMock = mock(Page.class);
    WebURL webUrlMock = mock(WebURL.class);
    when(webUrlMock.getURL()).thenReturn(url);
    when(pageMock.getWebURL()).thenReturn(webUrlMock);
    HtmlParseData parseDataMock = mock(HtmlParseData.class);
    when(pageMock.getParseData()).thenReturn(parseDataMock);
    when(parseDataMock.getTitle()).thenReturn(title);
    when(parseDataMock.getText()).thenReturn(text);
    crawler.visit(pageMock);
    assertThat(crawler.getDocsToIndex()).isEmpty();
  }

  @Test
  void visitGetLastModifiedFromPageHeader() {
    when(metadataDatabase.exists(anyString())).thenReturn(false);
    Page pageMock = mock(Page.class);
    WebURL webUrlMock = mock(WebURL.class);
    when(webUrlMock.getURL()).thenReturn("https://www.page1.com/xyz/abc.html");
    when(webUrlMock.getPath()).thenReturn("/xyz/abc.html");
    when(pageMock.getWebURL()).thenReturn(webUrlMock);
    HtmlParseData parseDataMock = mock(HtmlParseData.class);
    when(pageMock.getParseData()).thenReturn(parseDataMock);
    when(parseDataMock.getTitle()).thenReturn("Test page");
    when(parseDataMock.getText()).thenReturn("Text content");
    LocalDateTime date = LocalDateTime.of(2023, 1, 1, 10, 10, 30);
    Header header = new BasicHeader("Last-Modified",
        date.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME));
    when(pageMock.getFetchResponseHeaders()).thenReturn(new Header[]{header});
    crawler.visit(pageMock);
    assertThat(crawler.getDocsToIndex().get(0).lastUpdated()).isEqualTo(
        date.toEpochSecond(ZoneOffset.UTC));
  }

}