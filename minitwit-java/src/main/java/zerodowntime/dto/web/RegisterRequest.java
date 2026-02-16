package zerodowntime.dto.web;

public record RegisterRequest(
        String username,
        String email,
        String password,
        String passwordConfirm) {
}
