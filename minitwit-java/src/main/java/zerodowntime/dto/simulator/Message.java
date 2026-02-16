package zerodowntime.dto.simulator;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Message(
        @JsonProperty("content") String content,
        @JsonProperty("pub_date") String pubDate,
        @JsonProperty("user") String user) {
}