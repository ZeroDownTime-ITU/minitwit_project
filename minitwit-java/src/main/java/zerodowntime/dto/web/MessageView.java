package zerodowntime.dto.web;

public record MessageView(
        String username,
        String text,
        String pubDate, // Pre-formatted as "yyyy-MM-dd @ HH:mm"
        String gravatarUrl, // Full URL ready for <img src="...">
        int authorId) {
}