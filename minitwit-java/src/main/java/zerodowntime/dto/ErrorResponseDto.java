package zerodowntime.dto;

public record ErrorResponseDto(
        int status,
        String errorMsg) {
}