package zerodowntime.controller.web;

import java.util.List;
import java.util.Map;

import io.javalin.http.Context;
import zerodowntime.dto.web.MessageRequest;
import zerodowntime.dto.web.MessageView;
import zerodowntime.service.MessageService;
import zerodowntime.service.TimelineService;
import zerodowntime.service.UserService;
import zerodowntime.generated.jooq.tables.records.UserRecord;

public class UserController extends BaseController {
    private UserService userService;
    private MessageService messageService;
    private TimelineService timelineService;

    public UserController(UserService userService, MessageService messageService, TimelineService timelineService) {
        this.userService = userService;
        this.messageService = messageService;
        this.timelineService = timelineService;
    }

    // Display's a user's tweets.
    public void getUserProfile(Context ctx) {
        int pageOffset = getOffset(getPage(ctx));
        String username = ctx.pathParam("username");

        UserRecord profileUser = userService.getUserByUsername(username);
        if (profileUser == null) {
            ctx.status(404).json(Map.of("error", "User not found"));
            return;
        }

        Integer currentUserId = ctx.sessionAttribute("user_id");
        Integer profileUserId = profileUser.getUserId();

        List<MessageView> profileMessages = timelineService.getProfileMessages(profileUserId, pageOffset);
        boolean isFollowing = userService.isUserFollowingProfile(currentUserId, profileUserId);
        int messagesCount = timelineService.countProfileMessages(profileUserId);

        ctx.status(200).json(Map.of(
                "messages", profileMessages,
                "following", isFollowing,
                "total", messagesCount));
    }

    // Adds the current user as follower of the given user.
    public void handleFollow(Context ctx) {
        int userId = getAuthenticatedUserId(ctx);

        String usernameToFollow = ctx.pathParam("username");
        Integer userIdToFollow = userService.getUserIdByUsername(usernameToFollow);
        if (userIdToFollow == null) {
            ctx.status(404);
            return;
        }

        boolean followed = userService.followUser(userId, userIdToFollow);

        ctx.status(followed ? 204 : 200);
    }

    // Removes the current user as follower of the given user.
    public void handleUnfollow(Context ctx) {
        int userId = getAuthenticatedUserId(ctx);

        String usernameToUnfollow = ctx.pathParam("username");
        Integer userIdToUnfollow = userService.getUserIdByUsername(usernameToUnfollow);
        if (userIdToUnfollow == null) {
            ctx.status(404);
            return;
        }

        boolean unfollowed = userService.unfollowUser(userId, userIdToUnfollow);

        ctx.status(unfollowed ? 204 : 200);
    }

    // Registers a new message for the user.
    public void handlePostMessage(Context ctx) {
        int userId = getAuthenticatedUserId(ctx);

        MessageRequest request = ctx.bodyAsClass(MessageRequest.class);

        if (request.text() != null && !request.text().isEmpty()) {
            messageService.addMessage(userId, request.text());
            ctx.status(204);
        } else {
            ctx.status(400).json(Map.of("error", "Message text cannot be empty."));
        }
    }
}
