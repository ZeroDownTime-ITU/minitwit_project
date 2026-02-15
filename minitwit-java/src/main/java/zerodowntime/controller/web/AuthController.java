package zerodowntime.controller.web;

import java.util.List;
import java.util.Map;

import io.javalin.http.Context;
import zerodowntime.constants.AppConstants.PublicApi;
import zerodowntime.dto.web.RegisterRequest;
import zerodowntime.dto.web.LoginRequest;
import zerodowntime.dto.web.UserDto;
import zerodowntime.model.User;
import zerodowntime.service.AuthService;

public class AuthController extends BaseController {
    AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // public void showLogin(Context ctx) {
    // if (ctx.attribute("user") != null) {
    // ctx.redirect(PublicApi.HOME);
    // return;
    // }
    // ctx.render("login.html", createModel(ctx));
    // }

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

    // public void showRegister(Context ctx) {
    // if (ctx.attribute("user") != null) {
    // ctx.redirect(PublicApi.USER_TIMELINE);
    // return;
    // }
    // ctx.render("register.html", createModel(ctx));
    // }

    public void handleRegister(Context ctx) {
        RegisterRequest register = ctx.bodyAsClass(RegisterRequest.class);

        try {
            if (register.password() == null || !register.password().equals(register.passwordConfirm())) {
                throw new IllegalArgumentException("The two passwords do not match");
            }

            authService.registerUser(register.username(), register.email(), register.password());

            ctx.status(200);
            // ctx.sessionAttribute("flashes", List.of("You were successfully registered"));
            // ctx.redirect("/login");
        } catch (IllegalArgumentException e) {
            ctx.status(401).json(Map.of("error", e.getMessage()));
        }
    }

    public void handleLogout(Context ctx) {
        ctx.sessionAttribute("user_id", null);
        ctx.status(200);
        // ctx.sessionAttribute("flashes", List.of("You were logged out"));
        // ctx.redirect(PublicApi.PUBLIC_TIMELINE);
    }
}
