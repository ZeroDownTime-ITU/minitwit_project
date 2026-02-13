package zerodowntime.simulator;

import io.javalin.http.Context;
import zerodowntime.dto.ErrorResponseDto;
import zerodowntime.dto.RegisterRequestDto;
import zerodowntime.service.AuthService;

public class SimulatorController {
    AuthService authService;

    public SimulatorController(AuthService authService) {
        this.authService = authService;
    }

    public void register(Context ctx) {
        try {
            RegisterRequestDto request = ctx.bodyAsClass(RegisterRequestDto.class);
            authService.register(request.username(), request.email(), request.pwd());
            ctx.status(204);
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(new ErrorResponseDto(400, e.getMessage()));
        }
    }

}
