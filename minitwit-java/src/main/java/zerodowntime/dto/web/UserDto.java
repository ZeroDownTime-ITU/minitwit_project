package zerodowntime.dto.web;

public record UserDto(
        int userId,
        String username,
        String email) {
}