package zerodowntime.controller.web;

import java.util.Map;

import io.javalin.http.Context;
import io.javalin.http.Cookie;
import zerodowntime.dto.web.RegisterRequest;
import zerodowntime.dto.web.LoginRequest;
import zerodowntime.dto.web.UserDto;
import zerodowntime.service.AuthService;
import zerodowntime.service.UserService;
import zerodowntime.util.JwtUtils;
import zerodowntime.generated.jooq.tables.records.UserRecord;

public class AuthController extends BaseController {
    AuthService authService;
    UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    public void handleLogin(Context ctx) {
        LoginRequest login = ctx.bodyAsClass(LoginRequest.class);

        try {
            UserRecord user = authService.loginUser(login.username(), login.password());

            String token = JwtUtils.createToken(user.getUserId(), user.getUsername());
            setAuthCookie(ctx, token);

            ctx.status(200).json(new UserDto(user.getUserId(), user.getUsername(), user.getEmail()));
        } catch (IllegalArgumentException e) {
            ctx.status(401).json(Map.of("error", e.getMessage()));
        }
    }

    public void handleRegister(Context ctx) {
        RegisterRequest register = ctx.bodyAsClass(RegisterRequest.class);

        try {
            if (register.password() == null || !register.password().equals(register.passwordConfirm())) {
                throw new IllegalArgumentException("The two passwords do not match");
            }

            authService.registerUser(register.username(), register.email(), register.password());

            ctx.status(200);
        } catch (IllegalArgumentException e) {
            ctx.status(401).json(Map.of("error", e.getMessage()));
        }
    }

    public void getSession(Context ctx) {
        Integer userId = ctx.attribute("userId");
        String username = ctx.attribute("username");
        ctx.json(Map.of("userId", userId, "username", username));
    }

    public void handleLogout(Context ctx) {
        ctx.removeCookie("token", "/");
        ctx.status(200);
    }

    private void setAuthCookie(Context ctx, String token) {
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setSameSite(io.javalin.http.SameSite.LAX);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24); // 24 hours

        ctx.cookie(cookie);
    }
}
