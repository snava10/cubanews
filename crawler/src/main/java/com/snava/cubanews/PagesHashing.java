package com.snava.cubanews;

import com.snava.cubanews.data.access.SqliteMetadataDatabase;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.codec.digest.DigestUtils;

public class PagesHashing {
  private final Set<String> cache = new HashSet<>();
  SqliteMetadataDatabase db;

  public PagesHashing(SqliteMetadataDatabase db) {
    this.db = db;
  }

  public void initialise() {
    cache.clear();
    db.getAllHashes(10000000).forEach(cache::addAll);
  }

  public boolean pageExists(Page page) {
    HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
    String html = htmlParseData.getHtml();
    return pageExists(html);
  }

  public boolean pageExists(String pageContent) {
    String md5 = DigestUtils.md5Hex(pageContent);
    if (cache.contains(md5)) {
      return true;
    }
    return db.getByHash(md5).isPresent();
  }

  public void dispose() {
    cache.clear();
  }

  public String getHash(Page page) {
    HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
    String html = htmlParseData.getHtml();
    return DigestUtils.md5Hex(html);
  }

}
