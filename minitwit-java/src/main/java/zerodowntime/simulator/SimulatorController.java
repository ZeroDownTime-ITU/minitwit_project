package zerodowntime.simulator;

import java.util.Map;

import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;
import zerodowntime.dto.ErrorResponseDto;
import zerodowntime.dto.RegisterRequestDto;
import zerodowntime.service.AuthService;

public class SimulatorController {
    private AuthService authService;
    private static Integer latestValue = 0;

    public SimulatorController(AuthService authService) {
        this.authService = authService;
    }

    @OpenApi(path = "/api/register", methods = HttpMethod.POST, summary = "Register a new user", tags = {
            "Minitwit" }, queryParams = {
                    @OpenApiParam(name = "latest", type = Integer.class, description = "Latest value from simulator")
            }, requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = RegisterRequestDto.class)), responses = {
                    @OpenApiResponse(status = "204", description = "User created successfully"),
                    @OpenApiResponse(status = "400", content = @OpenApiContent(from = ErrorResponseDto.class))
            })
    public void register(Context ctx) {
        try {
            updateLatest(ctx);

            RegisterRequestDto request = ctx.bodyAsClass(RegisterRequestDto.class);
            authService.register(request.username(), request.email(), request.pwd());
            ctx.status(204);
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(new ErrorResponseDto(400, e.getMessage()));
        }
    }

    // Returns the latest ID saved
    public void latest(Context ctx) {
        try {
            ctx.json(Map.of("latest", latestValue));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponseDto(500, "Internal server error"));
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
