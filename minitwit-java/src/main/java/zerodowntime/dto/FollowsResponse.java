package zerodowntime.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record FollowsResponse(
        @JsonProperty("follows") List<String> follows) {
}