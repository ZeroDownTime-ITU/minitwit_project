package zerodowntime.dto.simulator;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterRequest(
        @JsonProperty("username") String username,
        @JsonProperty("email") String email,
        @JsonProperty("pwd") String pwd) {
}