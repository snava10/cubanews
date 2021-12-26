package com.snava.crawlandsearch;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AppConfig {

  @Bean
  public Searcher searcher() {
    return new Searcher();
  }

  @Bean
  public Crawler crawler() {
    return new CrawlerController("local-data/crawler4j");
  }

  @Bean
  RouterFunction<ServerResponse> routerFunction() {
    return route(request -> !request.uri().getPath().startsWith("/api/") && !request.uri().getPath()
            .equals("/") && !request.uri().getPath().contains("."),
        request -> ServerResponse.temporaryRedirect(URI.create("/"))
            .build());
  }
}
