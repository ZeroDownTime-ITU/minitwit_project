package zerodowntime.controller.web;

import java.util.List;
import io.javalin.http.Context;
import zerodowntime.model.User;
import zerodowntime.service.UserService;

public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Adds the current user as follower of the given user.
    public void handleFollow(Context ctx) {
        User currentUser = ctx.attribute("user");
        if (currentUser == null) {
            ctx.status(401);
            return;
        }

        String usernameToFollow = ctx.pathParam("username");
        Integer userIdToFollow = userService.getUserIdByUsername(usernameToFollow);
        if (userIdToFollow == null) {
            ctx.status(404);
            return;
        }

        userService.followUser(currentUser.getUserId(), userIdToFollow);

        ctx.sessionAttribute("flashes", List.of("You are now following \"" + usernameToFollow + "\""));
        ctx.redirect("/" + usernameToFollow);
    }
}
