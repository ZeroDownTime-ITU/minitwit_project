package zerodowntime.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterRequest(
        @JsonProperty("username") String username,
        @JsonProperty("email") String email,
        @JsonProperty("pwd") String pwd) {
}