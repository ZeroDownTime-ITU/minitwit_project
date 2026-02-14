package zerodowntime.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FollowAction(
        @JsonProperty("follow") String follow,
        @JsonProperty("unfollow") String unfollow) {
}