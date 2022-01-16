package com.snava.cubanews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
public class RestAPIApplication {

  public static void main(String[] args) {
    SpringApplication.run(RestAPIApplication.class, args);
  }

  @GetMapping("/api/health")
  public Mono<String> health(@RequestParam(value = "name", defaultValue = "World") String name) {
    System.out.println("Hitting hello endpoint");
    return Mono.just(String.format("Hello %s!", name));
  }
}

