package zerodowntime.dto.simulator;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PostMessage(
                @JsonProperty("content") String content) {
}