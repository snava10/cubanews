package com.snava.cubanews;

import java.io.IOException;
import java.nio.file.Paths;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Program {

  public static void main(String[] args) throws IOException, ParseException {

    final String indexPath = args[0];
    Directory index = FSDirectory.open(Paths.get(indexPath));
    Searcher searcher = new Searcher();
    for (Document doc :searcher.search("text", "cuba", index, 10)) {
      System.out.printf("%s %s%n", doc.get("url"), doc.get("title"));
      System.out.println(doc.get("text"));
    }
  }

}
