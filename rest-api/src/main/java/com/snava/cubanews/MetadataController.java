package com.snava.cubanews;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class MetadataController {

  @GetMapping("/api/metadata/countDocuments")
  public Mono<Long> countDocuments() {
    return Mono.just(0L);
  }

}
