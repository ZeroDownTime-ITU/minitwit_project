package zerodowntime.dto;

public record RegisterRequestDto(
        String username,
        String email,
        String pwd) {
}
