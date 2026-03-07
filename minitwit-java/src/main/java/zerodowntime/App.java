package zerodowntime;

import io.javalin.Javalin;
import static io.javalin.apibuilder.ApiBuilder.*;
import org.jooq.DSLContext;
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
import io.prometheus.client.Counter;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import java.io.StringWriter;

public class App {

    static final Counter httpRequests = Counter.build()
            .name("minitwit_http_requests_total")
            .help("Total antal HTTP requests")
            .labelNames("method", "path")
            .register();

    public static void main(String[] args) {
        DatabaseManager.init();
        createApp(DatabaseManager.getDsl()).start("0.0.0.0", 7070);
        System.out.println("Server started on http://0.0.0.0:7070");
    }

    public static Javalin createApp(DSLContext dsl) {
        // Create repositories
        var userRepo = new UserRepository(dsl);
        var messageRepo = new MessageRepository(dsl);
        var followerRepo = new FollowerRepository(dsl);

        // Create services
        AuthService authService = new AuthService(userRepo);
        TimelineService timelineService = new TimelineService(messageRepo);
        UserService userService = new UserService(userRepo, followerRepo);
        MessageService messageService = new MessageService(messageRepo, userRepo);

        // Create controllers
        AuthController authController = new AuthController(authService, userService);
        TimelineController timelineController = new TimelineController(timelineService);
        UserController userController = new UserController(userService, messageService, timelineService);
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

            config.concurrency.useVirtualThreads = false; // Disable virtual threads for better compatibility with JDBC

            config.routes.before("/*", ctx -> {
                httpRequests.labels(ctx.method().name(), ctx.path()).inc();
            });

            config.routes.before("/web/*", ctx -> {
                Integer userId = ctx.sessionAttribute("user_id");
                if (userId != null) {
                    userRepo.findById(userId).ifPresent(user -> ctx.attribute("user", user));
                }
            });

            config.routes.exception(Exception.class, (e, ctx) -> {
                System.err.println("[unhandled] " + e.getMessage());
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

                get("/metrics", ctx -> {
                    StringWriter sw = new StringWriter();
                    TextFormat.write004(sw, CollectorRegistry.defaultRegistry.metricFamilySamples());
                    ctx.contentType(TextFormat.CONTENT_TYPE_004).result(sw.toString());
                });

            });
        });

        return app;
    }
}