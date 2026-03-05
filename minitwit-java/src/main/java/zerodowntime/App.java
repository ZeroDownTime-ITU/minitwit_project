package zerodowntime;

import org.jdbi.v3.core.Jdbi;
import io.javalin.Javalin;
import static io.javalin.apibuilder.ApiBuilder.*;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import zerodowntime.constants.AppConstants.PublicApi;
import zerodowntime.constants.AppConstants.SimulatorApi;
import zerodowntime.controller.simulator.SimulatorController;
import zerodowntime.controller.web.AuthController;
import zerodowntime.controller.web.TimelineController;
import zerodowntime.controller.web.UserController;
import zerodowntime.dto.simulator.ErrorResponse;
import zerodowntime.repository.FollowerRepository;
import zerodowntime.repository.MessageRepository;
import zerodowntime.repository.UserRepository;
import zerodowntime.service.AuthService;
import zerodowntime.service.MessageService;
import zerodowntime.service.TimelineService;
import zerodowntime.service.UserService;

public class App {
    public static void main(String[] args) {
        Jdbi jdbi = DatabaseManager.createDatabase();
        createApp(jdbi).start("0.0.0.0", 7070);
        System.out.println("Server started on http://0.0.0.0:7070");
    }

    public static Javalin createApp(Jdbi jdbi) {
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

        Javalin app = Javalin.create(config -> {
            config.registerPlugin(new OpenApiPlugin(openApi -> {
                openApi.withDefinitionConfiguration((version, builder) -> {
                    builder.info(info -> info.title("Minitwit API").version("1.0.0"));
                });
            }));

            config.registerPlugin(new SwaggerPlugin(swagger -> {
                swagger.withUiPath("/swagger");
                swagger.withDocumentationPath("/openapi");
            }));

            config.concurrency.useVirtualThreads = true;

            config.routes.before("/api/*", ctx -> {
                Integer userId = ctx.sessionAttribute("user_id");
                if (userId != null) {
                    userRepo.findById(userId).ifPresent(user -> ctx.attribute("user", user));
                }
            });

            config.routes.exception(Exception.class, (e, ctx) -> {
                e.printStackTrace();
                ctx.status(500).json(new ErrorResponse(500, "Internal server error"));
            });

            config.routes.apiBuilder(() -> {
                // ============ WEB APP ROUTES ============
                // Auth
                post(PublicApi.LOGIN, authController::handleLogin);
                post(PublicApi.REGISTER, authController::handleRegister);
                post(PublicApi.LOGOUT, authController::handleLogout);
                get(PublicApi.SESSION, authController::getSession);

                // Timeline
                get(PublicApi.USER_TIMELINE, timelineController::getUserTimeline);
                get(PublicApi.PUBLIC_TIMELINE, timelineController::getPublicTimeline);

                // User
                get(PublicApi.USER_PROFILE, userController::getUserProfile);
                post(PublicApi.POSTMESSAGE, userController::handlePostMessage);
                post(PublicApi.FOLLOW, userController::handleFollow);
                post(PublicApi.UNFOLLOW, userController::handleUnfollow);

                // ============ SIMULATOR API ROUTES ============
                post(SimulatorApi.REGISTER, simController::postRegister);
                get(SimulatorApi.LATEST, simController::getLatest);
                post(SimulatorApi.MSGS_USER, simController::postMessage);
                get(SimulatorApi.FLLWS_USER, simController::getFollowers);
                post(SimulatorApi.FLLWS_USER, simController::postFollow);
                get(SimulatorApi.MSGS, simController::getRecentMessages);
                get(SimulatorApi.MSGS_USER, simController::getMessagesUser);
            });
        });

        return app;
    }
}