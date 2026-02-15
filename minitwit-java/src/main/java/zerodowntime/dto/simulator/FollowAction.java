package zerodowntime.dto.simulator;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FollowAction(
                @JsonProperty("follow") String follow,
                @JsonProperty("unfollow") String unfollow) {
}