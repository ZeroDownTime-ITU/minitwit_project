package zerodowntime.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ErrorResponse(
        @JsonProperty("status") Integer status,
        @JsonProperty("error_msg") String errorMsg) {
}