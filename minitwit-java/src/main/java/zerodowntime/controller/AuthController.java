package zerodowntime.controller;

import java.util.List;
import java.util.Map;

import io.javalin.http.Context;
import zerodowntime.constants.AppConstants.Web;
import zerodowntime.model.User;
import zerodowntime.service.AuthService;

public class AuthController extends BaseController {
    AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public void showLogin(Context ctx) {
        if (ctx.attribute("user") != null) {
            ctx.redirect(Web.HOME);
            return;
        }
        ctx.render("login.html", createModel(ctx));
    }

    public void handleLogin(Context ctx) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        try {
            User user = authService.login(username, password);

            ctx.sessionAttribute("flashes", List.of("You were logged in"));
            ctx.sessionAttribute("user_id", user.getUserId());
            ctx.redirect(Web.HOME);
        } catch (IllegalArgumentException e) {
            Map<String, Object> model = createModel(ctx);
            model.put("error", e.getMessage());
            ctx.render("login.html", model);
        }
    }

    public void showRegister(Context ctx) {
        if (ctx.attribute("user") != null) {
            ctx.redirect(Web.HOME);
            return;
        }
        ctx.render("register.html", createModel(ctx));
    }

    public void handleRegister(Context ctx) {
        String username = ctx.formParam("username");
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        String passwordConfirm = ctx.formParam("password2");

        try {
            if (password == null || !password.equals(passwordConfirm)) {
                throw new IllegalArgumentException("The two passwords do not match");
            }

            authService.register(username, email, password);

            ctx.sessionAttribute("flashes", List.of("You were successfully registered"));
            ctx.redirect("/login");
        } catch (IllegalArgumentException e) {
            Map<String, Object> model = createModel(ctx);
            model.put("error", e.getMessage());
            ctx.render("register.html", model);
        }
    }

    public void handleLogout(Context ctx) {
        ctx.sessionAttribute("user_id", null);
        ctx.sessionAttribute("flashes", List.of("You were logged out"));
        ctx.redirect(Web.PUBLIC);
    }
}
