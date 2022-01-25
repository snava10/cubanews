package com.snava.cubanews;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public class DocumentJsonSerializer extends JsonSerializer<IndexDocument> {

  @Override
  public void serialize(IndexDocument value, JsonGenerator gen,
      SerializerProvider serializers) throws IOException {
    gen.writeStartObject();
    gen.writeStringField("url", value.url());
    gen.writeStringField("title", value.title());
    gen.writeStringField("text", value.text());
    gen.writeNumberField("lastUpdated", value.lastUpdated());
    gen.writeEndObject();
  }
}
