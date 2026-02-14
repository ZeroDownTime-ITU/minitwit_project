package zerodowntime.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PostMessage(
        @JsonProperty("content") String content) {
}