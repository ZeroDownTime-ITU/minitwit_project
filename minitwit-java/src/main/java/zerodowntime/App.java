package zerodowntime;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    // Configuration
    private static final String DEFAULT_DB_PATH = "data/minitwit-java.db";
    public static String database = "jdbc:sqlite:" + DEFAULT_DB_PATH;
    public static final Jdbi jdbi = Jdbi.create(database).installPlugin(new SqlObjectPlugin());
    public static final String SECRET_KEY = "development key";

    @Deprecated
    // Returns a new connection to the database
    public static Connection connectDb() throws SQLException {
        return DriverManager.getConnection(database);
    }

    // Creates the database tables
    public static void initDb() throws Exception {
        InputStream inputStream = App.class.getResourceAsStream("/schema.sql");
        String sql = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        try (Connection db = connectDb(); var stmt = db.createStatement()) {
            for (String command : sql.split(";")) {
                if (!command.trim().isEmpty()) {
                    stmt.executeUpdate(command.trim());
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Path dbPath = Paths.get(DEFAULT_DB_PATH);

        if (Files.exists(dbPath)) {
            System.out.println("Database already exists at: " + DEFAULT_DB_PATH);
        } else {
            Files.createDirectories(dbPath.getParent());

            System.out.println("Database not found. Initializing...");
            initDb();
            System.out.println("Database initialized successfully.");
        }

        createApp().start(7070); // Start server
    }

    public static Javalin createApp() {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/static";
                staticFiles.directory = "/static";
            });

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
        SimulatorController simController = new SimulatorController(authService, userService);

        // Lookup the current user so that we know he's there
        app.before(context -> {
            // Load user from session
            Integer userId = context.sessionAttribute("user_id");
            if (userId != null) {
                userRepo.findById(userId).ifPresent(user -> {
                    context.attribute("user", user);
                });
            } else {
                context.attribute("user", null);
            }
        });

        // ============ WEB APP ROUTES ============

        // Auth routes
        app.post(PublicApi.LOGIN, authController::handleLogin);
        app.post(PublicApi.REGISTER, authController::handleRegister);
        app.post(PublicApi.LOGOUT, authController::handleLogout);
        app.get(PublicApi.SESSION, authController::getSession);

        // Timeline routes
        app.get(PublicApi.USER_TIMELINE, timelineController::getUserTimeline);
        app.get(PublicApi.PUBLIC_TIMELINE, timelineController::getPublicTimeline);

        // User routes
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