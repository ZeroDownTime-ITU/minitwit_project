package zerodowntime.controller.web;

import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import zerodowntime.model.User;

public class BaseController {

    /**
     * Get authenticated user ID or throw 401
     */
    protected int getAuthenticatedUserId(Context ctx) {
        Integer userId = ctx.sessionAttribute("user_id");
        if (userId == null) {
            throw new UnauthorizedResponse("You must be logged in.");
        }
        return userId;
    }

    /**
     * Get authenticated user or throw 401
     */
    protected User getAuthenticatedUser(Context ctx) {
        User user = ctx.attribute("user"); // From before filter
        if (user == null) {
            throw new UnauthorizedResponse("You must be logged in.");
        }
        return user;
    }
}