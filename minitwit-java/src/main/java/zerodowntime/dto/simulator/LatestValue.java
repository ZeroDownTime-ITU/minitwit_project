package zerodowntime.dto.simulator;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LatestValue(
        @JsonProperty("latest") Integer latest) {
}