package zerodowntime;

import org.jdbi.v3.core.Jdbi;
import io.javalin.Javalin;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import zerodowntime.constants.AppConstants.PublicApi;
import zerodowntime.constants.AppConstants.SimulatorApi;
import zerodowntime.controller.simulator.SimulatorController;
import zerodowntime.controller.web.AuthController;
import zerodowntime.controller.web.TimelineController;
import zerodowntime.controller.web.UserController;
import zerodowntime.repository.FollowerRepository;
import zerodowntime.repository.MessageRepository;
import zerodowntime.repository.UserRepository;
import zerodowntime.service.AuthService;
import zerodowntime.service.MessageService;
import zerodowntime.service.TimelineService;
import zerodowntime.service.UserService;

public class App {
    public static void main(String[] args) {
        // Create production database (auto-initializes if needed)
        Jdbi jdbi = DatabaseManager.createDatabase("jdbc:sqlite:data/minitwit-java.db");

        // Start server
        createApp(jdbi).start(7070);

        System.out.println("Server started on http://localhost:7070");
    }

    public static Javalin createApp(Jdbi jdbi) {
        Javalin app = Javalin.create(config -> {
            // 1. Configure OpenAPI (The generation)
            config.registerPlugin(new OpenApiPlugin(openApiConfig -> {
                openApiConfig.withDefinitionConfiguration((version, definition) -> {
                    definition.withInfo(info -> {
                        info.setTitle("Minitwit API");
                        info.setVersion("1.0.0");
                    });
                });
            }));

            // 2. Configure Swagger (The UI)
            config.registerPlugin(new SwaggerPlugin(swaggerConfig -> {
                swaggerConfig.setUiPath("/swagger");
                swaggerConfig.setDocumentationPath("/openapi");
            }));
        });

        // Create repositories
        UserRepository userRepo = jdbi.onDemand(UserRepository.class);
        MessageRepository messageRepo = jdbi.onDemand(MessageRepository.class);
        FollowerRepository followerRepo = jdbi.onDemand(FollowerRepository.class);

        // Create services
        AuthService authService = new AuthService(userRepo);
        TimelineService timelineService = new TimelineService(messageRepo);
        UserService userService = new UserService(userRepo, followerRepo, messageRepo);
        MessageService messageService = new MessageService(messageRepo, userRepo);

        // Create controllers
        AuthController authController = new AuthController(authService, userService);
        TimelineController timelineController = new TimelineController(timelineService);
        UserController userController = new UserController(userService, messageService);
        SimulatorController simController = new SimulatorController(authService, userService, messageService);

        app.before("/api/*", ctx -> { // ← Only for /api/* routes
            Integer userId = ctx.sessionAttribute("user_id");
            if (userId != null) {
                userRepo.findById(userId).ifPresent(user -> {
                    ctx.attribute("user", user);
                });
            }
        });

        // ============ WEB APP ROUTES ============

        // Auth
        app.post(PublicApi.LOGIN, authController::handleLogin);
        app.post(PublicApi.REGISTER, authController::handleRegister);
        app.post(PublicApi.LOGOUT, authController::handleLogout);
        app.get(PublicApi.SESSION, authController::getSession);

        // Timeline
        app.get(PublicApi.USER_TIMELINE, timelineController::getUserTimeline);
        app.get(PublicApi.PUBLIC_TIMELINE, timelineController::getPublicTimeline);

        // User
        app.get(PublicApi.USER_PROFILE, userController::getUserProfile);
        app.post(PublicApi.POSTMESSAGE, userController::handlePostMessage);
        app.post(PublicApi.FOLLOW, userController::handleFollow);
        app.post(PublicApi.UNFOLLOW, userController::handleUnfollow);

        // ============ SIMULATOR API ROUTES ============

        app.post(SimulatorApi.REGISTER, simController::postRegister);
        app.get(SimulatorApi.LATEST, simController::getLatest);
        app.post(SimulatorApi.MSGS_USER, simController::postMessage);
        app.get(SimulatorApi.FLLWS_USER, simController::getFollowers);
        app.post(SimulatorApi.FLLWS_USER, simController::postFollow);

        return app;
    }
}