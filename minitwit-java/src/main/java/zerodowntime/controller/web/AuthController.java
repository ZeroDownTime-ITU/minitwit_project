package zerodowntime.controller.web;

import java.util.Map;

import io.javalin.http.Context;
import zerodowntime.dto.web.RegisterRequest;
import zerodowntime.dto.web.LoginRequest;
import zerodowntime.dto.web.UserDto;
import zerodowntime.model.User;
import zerodowntime.service.AuthService;
import zerodowntime.service.UserService;

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
            User user = authService.loginUser(login.username(), login.password());
            UserDto userDto = new UserDto(
                    user.getUserId(),
                    user.getUsername(),
                    user.getEmail());

            ctx.sessionAttribute("user_id", user.getUserId());

            ctx.status(200).json(userDto);
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
        Integer userId = ctx.sessionAttribute("user_id");

        if (userId == null) {
            ctx.status(401);
            return;
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            ctx.status(401);
            return;
        }

        ctx.json(Map.of(
                "userId", user.getUserId(),
                "username", user.getUsername(),
                "email", user.getEmail()));
    }

    public void handleLogout(Context ctx) {
        ctx.sessionAttribute("user_id", null);
        ctx.status(200);
    }
}
