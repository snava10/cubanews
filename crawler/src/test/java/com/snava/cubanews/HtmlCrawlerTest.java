package com.snava.cubanews;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HtmlCrawlerTest {

  HtmlCrawler crawler;
  Indexer indexer;

  @BeforeEach
  public void setup() {
    indexer = mock(Indexer.class);
    crawler = new HtmlCrawler(indexer, Stream.of(
        "https://url1.com/",
        "https://url2.com/"
    ).collect(Collectors.toSet()), null);
  }

  @Test
  void visit() {
  }

  @ParameterizedTest
  @CsvSource({",,", "  ,  ,  "})
  void visit_InvalidDocument(String url, String title, String text) {
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
}