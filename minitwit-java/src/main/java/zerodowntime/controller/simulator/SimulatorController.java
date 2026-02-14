package zerodowntime.controller.simulator;

import java.util.List;
import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;
import zerodowntime.dto.ErrorResponse;
import zerodowntime.dto.FollowAction;
import zerodowntime.dto.FollowsResponse;
import zerodowntime.dto.LatestValue;
import zerodowntime.dto.PostMessage;
import zerodowntime.dto.RegisterRequest;
import zerodowntime.service.AuthService;
import zerodowntime.service.UserService;

public class SimulatorController {
    private AuthService authService;
    private UserService userService;

    private static Integer latestValue = 0;

    public SimulatorController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @OpenApi(
        path = "/api/fllws/{username}",
        methods = HttpMethod.GET,
        summary = "Get list of users followed by the given user",
        tags = { "Minitwit" },
        pathParams = { @OpenApiParam(name = "username", description = "The username to look up", required = true) },
        queryParams = {
                @OpenApiParam(name = "latest", type = Integer.class, description = "Optional: latest value to update"),
                @OpenApiParam(name = "no", type = Integer.class, description = "Optional: limits result count")
        },
        headers = { @OpenApiParam(name = "Authorization", description = "Basic simulator auth", required = true) },
        responses = {
                @OpenApiResponse(status = "200", content = @OpenApiContent(from = FollowsResponse.class)),
                @OpenApiResponse(status = "403", content = @OpenApiContent(from = ErrorResponse.class)),
                @OpenApiResponse(status = "404", description = "User not found")
            }
        )
    public void getFollowers(Context ctx) {
        updateLatest(ctx);

        String authHeader = ctx.header("Authorization");
        if (authHeader == null || !authHeader.equals("Basic c2ltdWxhdG9yOnN1cGVyX3NhZmUh")) {
            ctx.status(403).json(new ErrorResponse(403, "Unauthorized - Must include correct Authorization header"));
            return;
        }

        String username = ctx.pathParam("username");
        int limit = ctx.queryParamAsClass("no", Integer.class).getOrDefault(100);

        Integer currentUser = userService.getUserIdByUsername(username);
        if (currentUser == null) {
            ctx.status(404);
            return;
        }

        List<String> followingNames = userService.getUserFollowing(username, limit);

        ctx.json(new FollowsResponse(followingNames));
    }

   @OpenApi(
        path = "/api/fllws/{username}",
        methods = HttpMethod.POST,
        summary = "Follow or unfollow a user",
        tags = { "Minitwit" },
        pathParams = { @OpenApiParam(name = "username", required = true)},
        queryParams = { @OpenApiParam(name = "latest", type = Integer.class, description = "Optional: latest value to update") },
        headers = { @OpenApiParam(name = "Authorization", description = "Basic c2ltdWxhdG9yOnN1cGVyX3NhZmUh", required = true) },
        requestBody = @OpenApiRequestBody(
            content = @OpenApiContent(from = FollowAction.class),
            required = true
        ),
        responses = {
            @OpenApiResponse(status = "204", description = "No Content"),
            @OpenApiResponse(status = "403", content = @OpenApiContent(from = ErrorResponse.class)),
            @OpenApiResponse(status = "404", description = "User not found")
        }
    )
    public void postFollow(Context ctx) {
        updateLatest(ctx);

        String authHeader = ctx.header("Authorization");
        if (authHeader == null || !authHeader.equals("Basic c2ltdWxhdG9yOnN1cGVyX3NhZmUh")) {
            ctx.status(403).json(new ErrorResponse(403, "Unauthorized - Must include correct Authorization header"));
            return;
        }

        String username = ctx.pathParam("username");
        FollowAction action = ctx.bodyAsClass(FollowAction.class);

        Integer currentUser = userService.getUserIdByUsername(username);
        if (currentUser == null) {
            ctx.status(404);
            return;
        }

        if (action.follow() != null) {
            Integer userToFollow = userService.getUserIdByUsername(action.follow());
            if (userToFollow == null) { ctx.status(404); return; }
            
            userService.followUser(currentUser, userToFollow);
        } 
        // else if (action.unfollow() != null) {
        //     Integer whomId = userService.getUserIdByUsername(action.unfollow());
        //     if (whomId == null) { ctx.status(404); return; }
            
        //     userService.unfollowUser(whoId, whomId);
        // }

        ctx.status(204);
    }

    @OpenApi(
        path = "/api/latest",
        methods = HttpMethod.GET,
        summary = "Returns the latest ID saved",
        tags = { "Minitwit" },
        responses = {
            @OpenApiResponse(status = "200", description = "Success", content = @OpenApiContent(from = LatestValue.class)),
            @OpenApiResponse(status = "500", description = "Internal Server Error", content = @OpenApiContent(from = ErrorResponse.class))
        }
    )
    public void getLatest(Context ctx) {
        try {
            ctx.json(new LatestValue(latestValue)); 
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse(500, "Internal server error"));
        }
    }

    public void getRecentMessages(Context ctx) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void getMessagesUser(Context ctx) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @OpenApi(
        path = "/api/register",
        methods = HttpMethod.POST,
        summary = "Register a new user",
        tags = { "Minitwit" },
        queryParams = { @OpenApiParam(name = "latest", type = Integer.class, description = "Latest value from simulator")},
        requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = RegisterRequest.class)),
        responses = {
            @OpenApiResponse(status = "204", description = "User created successfully"),
            @OpenApiResponse(status = "400", content = @OpenApiContent(from = ErrorResponse.class))
        }
    )
    public void postRegister(Context ctx) {
        try {
            updateLatest(ctx);

            RegisterRequest request = ctx.bodyAsClass(RegisterRequest.class);
            authService.registerUser(request.username(), request.email(), request.pwd());
            ctx.status(204);
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(new ErrorResponse(400, e.getMessage()));
        }
    }

    @OpenApi(
        path = "/api/msgs/{username}",
        methods = HttpMethod.POST,
        summary = "Post a new message as a specific user",
        tags = { "Minitwit" },
        pathParams = { @OpenApiParam(name = "username", type = String.class, required = true) },
        queryParams = { @OpenApiParam(name = "latest", type = Integer.class, description = "Optional: latest value to update")},
        headers = { @OpenApiParam(name = "Authorization", type = String.class, required = true, description = "Basic c2ltdWxhdG9yOnN1cGVyX3NhZmUh")},
        requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = PostMessage.class)),
        responses = {
            @OpenApiResponse(status = "204", description = "No Content"),
            @OpenApiResponse(status = "403", content = @OpenApiContent(from = ErrorResponse.class))
        }
    )
    public void postMessage(Context ctx) {
        updateLatest(ctx);

        String authHeader = ctx.header("Authorization");
        if (authHeader == null || !authHeader.equals("Basic c2ltdWxhdG9yOnN1cGVyX3NhZmUh")) {
            ctx.status(403).json(new ErrorResponse(403, "Unauthorized - Must include correct Authorization header"));
            return;
        }

        String username = ctx.pathParam("username");
        PostMessage payload = ctx.bodyAsClass(PostMessage.class);
        
        try {
            Integer userId = userService.getUserIdByUsername(username); 
            if (userId == null) {
                ctx.status(403); //Not sure what to do here. Should be 404 but simulator expects only seems to expect 403.
                return;
            }

            long currentTime = System.currentTimeMillis() / 1000;
            // messageService.postMessage(userId, payload.content(), currentTime);
            
            ctx.status(204);
        } catch (Exception e) {
            ctx.status(403).json(new ErrorResponse(403, "Could not post message")); // same here, should be 400;
        }
    }

    private void updateLatest(Context ctx) {
        String latestParam = ctx.queryParam("latest");
        if (latestParam != null) {
            try {
                latestValue = Integer.parseInt(latestParam);
            } catch (NumberFormatException e) {
                // Log the error and ignore the invalid input
                System.err.println("Invalid 'latest' parameter: " + latestParam);
            }
        }
    }

}
