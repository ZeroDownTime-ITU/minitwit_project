package zerodowntime.dto.web;

import java.util.List;

public record UserProfileData(
        List<MessageView> messages,
        boolean followed) {
}