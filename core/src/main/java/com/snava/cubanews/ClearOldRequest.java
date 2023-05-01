package com.snava.cubanews;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import org.immutables.value.Value.Immutable;

@Immutable
@JsonSerialize(as = ImmutableClearOldRequest.class)
@JsonDeserialize(as = ImmutableClearOldRequest.class)
public interface ClearOldRequest {
    List<ClearOldRequestData> data();
}
