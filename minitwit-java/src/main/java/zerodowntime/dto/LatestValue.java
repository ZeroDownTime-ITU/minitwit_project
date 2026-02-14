package zerodowntime.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LatestValue(
        @JsonProperty("latest") Integer latest) {
}