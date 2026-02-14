package zerodowntime.controller.simulator;

import java.util.Map;

import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;
import zerodowntime.dto.ErrorResponse;
import zerodowntime.dto.PostMessage;
import zerodowntime.dto.RegisterRequest;
import zerodowntime.service.AuthService;

public class SimulatorController {
    private AuthService authService;
    private static Integer latestValue = 0;

    public SimulatorController(AuthService authService) {
        this.authService = authService;
    }

    @OpenApi(
        path = "/api/register",
        methods = HttpMethod.POST,
        summary = "Register a new user",
        tags = { "Minitwit" },
        queryParams = {
            @OpenApiParam(name = "latest", type = Integer.class, description = "Latest value from simulator")
        },
        requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = RegisterRequest.class)),
        responses = {
            @OpenApiResponse(status = "204", description = "User created successfully"),
            @OpenApiResponse(status = "400", content = @OpenApiContent(from = ErrorResponse.class))
        }
    )
    public void register(Context ctx) {
        try {
            updateLatest(ctx);

            RegisterRequest request = ctx.bodyAsClass(RegisterRequest.class);
            authService.registerUser(request.username(), request.email(), request.pwd());
            ctx.status(204);
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(new ErrorResponse(400, e.getMessage()));
        }
    }

    // Returns the latest ID saved
    public void latest(Context ctx) {
        try {
            ctx.json(Map.of("latest", latestValue));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse(500, "Internal server error"));
        }
    }

    @OpenApi(
        path = "/api/msgs/{username}",
        methods = HttpMethod.POST,
        summary = "Post a new message as a specific user",
        tags = {"Minitwit"},
        pathParams = {
            @OpenApiParam(name = "username", type = String.class, required = true)
        },
        queryParams = {
            @OpenApiParam(name = "latest", type = Integer.class, description = "Optional: latest value to update")
        },
        headers = {
            @OpenApiParam(name = "Authorization", type = String.class, required = true, description = "Basic c2ltdWxhdG9yOnN1cGVyX3NhZmUh")},
        requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = PostMessage.class)),
        responses = {
            @OpenApiResponse(status = "204", description = "No Content"),
            @OpenApiResponse(status = "403", content = @OpenApiContent(from = ErrorResponse.class))
        }
    )
    public void post(Context ctx) {
        updateLatest(ctx);

        String authHeader = ctx.header("Authorization");
        if (authHeader == null || !authHeader.equals("Basic c2ltdWxhdG9yOnN1cGVyX3NhZmUh")) {
            ctx.status(403).json(new ErrorResponse(403, "Unauthorized - Must include correct Authorization header"));
            return;
        }

        String username = ctx.pathParam("username");
        PostMessage payload = ctx.bodyAsClass(PostMessage.class);
        
        try {
            Integer userId = authService.getUserIdByUsername(username); 
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

    // Get recent messages.
    // Filters out flagged messages
    // Returns a list of recent messages (max defined by `?no=` param)
    // Optionally updates a 'latest' global value via `?latest=` query param.
    public void messages(Context ctx) {
        ctx.result("Hello from the simulator!");
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
