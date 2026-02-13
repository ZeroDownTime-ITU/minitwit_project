package zerodowntime.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class FormatUtils {

    public static String formatDatetime(long timestamp) {
        return Instant.ofEpochSecond(timestamp)
                .atZone(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd @ HH:mm"));
    }

    public static String gravatarUrl(String email, int size) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(email.trim().toLowerCase().getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return "http://www.gravatar.com/avatar/" + hex + "?d=identicon&s=" + size;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}