package com.snava.crawlandsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class CrawlAndSearchRestAPIApplication {

  public static void main(String[] args) {
    SpringApplication.run(CrawlAndSearchRestAPIApplication.class, args);
  }

  @GetMapping("/health")
  public String health(@RequestParam(value = "name", defaultValue = "World") String name) {
    System.out.println("Hitting hello endpoint");
    return String.format("Hello %s!", name);
  }

}

