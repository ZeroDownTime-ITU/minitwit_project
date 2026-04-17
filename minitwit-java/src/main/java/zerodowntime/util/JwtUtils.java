package zerodowntime.util;

import java.time.Instant;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import io.javalin.http.Context;

public class JwtUtils {
    private JwtUtils() {}

    private static final Algorithm ALGORITHM = Algorithm.HMAC256(System.getenv("JWT_SECRET"));

    public static String createToken(int userId, String username) {
        return JWT.create()
                .withClaim("userId", userId)
                .withClaim("username", username)
                .withExpiresAt(Instant.now().plusSeconds(60L * 60 * 24)) // 24h
                .sign(ALGORITHM);
    }

    public static DecodedJWT verifyToken(String token) {
        return JWT.require(ALGORITHM).build().verify(token);
    }

    public static int getUserId(Context ctx) {
        return ctx.attribute("userId");
    }
}
