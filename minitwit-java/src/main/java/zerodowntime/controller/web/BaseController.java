package zerodowntime.controller.web;

import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import zerodowntime.constants.AppConstants;

public class BaseController {

    /**
     * Get authenticated user ID or throw 401
     */
    protected int getAuthenticatedUserId(Context ctx) {
        Integer userId = ctx.attribute("userId");
        if (userId == null) {
            throw new UnauthorizedResponse("You must be logged in.");
        }
        return userId;
    }

    protected int getPage(Context ctx) {
        return Integer.parseInt(ctx.queryParam("page") != null ? ctx.queryParam("page") : "0");
    }

    protected int getOffset(int page) {
        return page * AppConstants.PER_PAGE;
    }
}