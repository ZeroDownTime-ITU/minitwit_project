package zerodowntime;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import io.javalin.rendering.template.JavalinPebble;
import zerodowntime.constants.AppConstants.Api;
import zerodowntime.constants.AppConstants.Web;
import zerodowntime.controller.simulator.SimulatorController;
import zerodowntime.controller.web.AuthController;
import zerodowntime.controller.web.TimelineController;
import zerodowntime.controller.web.UserController;
import zerodowntime.model.User;
import zerodowntime.repository.FollowerRepository;
import zerodowntime.repository.MessageRepository;
import zerodowntime.repository.UserRepository;
import zerodowntime.service.AuthService;
import zerodowntime.service.TimelineService;
import zerodowntime.service.UserService;

public class App {

    // Configuration
    private static final String DEFAULT_DB_PATH = "data/minitwit-java.db";
    public static String database = "jdbc:sqlite:" + DEFAULT_DB_PATH;
    public static final Jdbi jdbi = Jdbi.create(database).installPlugin(new SqlObjectPlugin());

    @Deprecated // in appconstants now
    public static final int PER_PAGE = 30;
    public static final boolean DEBUG = true;
    public static final String SECRET_KEY = "development key";

    @Deprecated
    private static final String ROUTE_HOME = "/";

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

    @Deprecated
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> queryDb(String query, Object... args) {
        return (List<Map<String, Object>>) queryDbInternal(query, false, args);
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    public static Map<String, Object> queryDbOne(String query, Object... args) {
        return (Map<String, Object>) queryDbInternal(query, true, args);
    }

    @Deprecated
    // Queries the database and returns a list of dictionaries
    private static Object queryDbInternal(String query, boolean one, Object... args) {
        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection db = connectDb(); var stmt = db.prepareStatement(query)) {
            for (int i = 0; i < args.length; i++) {
                stmt.setObject(i + 1, args[i]);
            }

            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Returns first result if one == true, else return full list
        if (one) {
            return results.isEmpty() ? null : results.get(0);
        }
        return results;
    }

    // Convenience method to look up the id for a username.
    public static Integer getUserId(String username) {
        Map<String, Object> rv = queryDbOne("SELECT user_id FROM user WHERE username = ?", username);
        if (rv != null) {
            return (Integer) rv.get("user_id");
        } else {
            return null;
        }
    }

    @Deprecated // in utils now
    // Format a timestamp for display.
    public static String formatDatetime(long timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        ZonedDateTime dt = instant.atZone(ZoneOffset.UTC);
        return dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd @ HH:mm"));
    }

    @Deprecated // new one in utils
    // Return the gravatar image for the given email address
    public static String gravatarUrl(String email, Integer size) {
        if (size == null) {
            size = 80;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(email.trim().toLowerCase().getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return "http://www.gravatar.com/avatar/" + hex + "?d=identicon&s=" + size;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Helper method to hydrate message data with gravatar URLs and formatted dates
    private static void hydrateMessages(List<Map<String, Object>> messages) {
        for (Map<String, Object> message : messages) {
            String email = (String) message.get("email");
            Object pubDateObj = message.get("pub_date");
            long pubDate = pubDateObj instanceof Number ? ((Number) pubDateObj).longValue() : 0;

            message.put("gravatar_url", gravatarUrl(email, 48));
            message.put("formatted_date", formatDatetime(pubDate));
        }
    }

    // Helper method to create base model with g.user and flashes
    private static Map<String, Object> createModel(Context context) {
        Map<String, Object> model = new HashMap<>();

        // Add g.user (Flask-style)
        Map<String, Object> g = new HashMap<>();
        g.put("user", context.attribute("user"));
        model.put("g", g);

        // Add flashes
        Object flashes = context.attribute("flashes");
        if (flashes != null) {
            model.put("flashes", flashes);
        }

        return model;
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
            config.fileRenderer(new JavalinPebble()); // Use Pebble for rendering HTML templates

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
        TimelineService timelineService = new TimelineService(messageRepo); // needs user repo later
        UserService userService = new UserService(userRepo, followerRepo);
        // MessageService messageService = new MessageService(messageRepo);

        // Create controllers
        AuthController authController = new AuthController(authService);
        TimelineController timelineController = new TimelineController(timelineService);
        UserController userController = new UserController(userService);
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

            // Load flashes from session and consume them
            List<String> flashes = context.sessionAttribute("flashes");
            if (flashes != null) {
                context.attribute("flashes", flashes);
                context.consumeSessionAttribute("flashes");
            }
        });

        // ============ WEB APP ROUTES ============

        // Auth routes
        app.get(Web.LOGIN, authController::showLogin);
        app.post(Web.LOGIN, authController::handleLogin);
        app.get(Web.REGISTER, authController::showRegister);
        app.post(Web.REGISTER, authController::handleRegister);
        app.get(Web.LOGOUT, authController::handleLogout);

        // Timeline routes
        app.get(Web.HOME, timelineController::showUserTimeline);
        app.get(Web.PUBLIC, timelineController::showPublicTimeline);

        // User routes
        // app.get(Web.USER_PROFILE, userController::showUserProfile);
        app.get(Web.FOLLOW, userController::handleFollow);
        //app.get(Web.UNFOLLOW, userController::handleUnfollow);


        // ============ SIMULATOR API ROUTES ============

        app.post(Api.REGISTER, simController::postRegister);
        app.get(Api.LATEST, simController::getLatest);
        app.post(Api.MSGS_USER, simController::postMessage);
        app.get(Api.FLLWS_USER, simController::getFollowers);
        app.post(Api.FLLWS_USER, simController::postFollow);

        // ============ TODO: BELOW ALL STILL NEED TO BE REWORKED LIKE THE ONES ABOVE ============

        // Registers a new message for the user.
        app.post("/add_message", context -> {
            if (context.sessionAttribute("user_id") == null) // Check if user is logged in
            {
                context.status(401); // Sends a not authorized response
                return;
            }

            String text = context.formParam("text");

            // Check if string exists through null check and by checking if string is empty
            if (text != null && !text.isEmpty()) {
                Integer userId = context.sessionAttribute("user_id"); // Get User ID
                long currentTime = System.currentTimeMillis() / 1000; // Get timestamp, convert from ms to seconds

                String sql = "INSERT INTO message (author_id, text, pub_date, flagged) VALUES (?, ?, ?, 0)";
                try (Connection db = connectDb(); var stmt = db.prepareStatement(sql)) {
                    stmt.setInt(1, userId);
                    stmt.setString(2, text);
                    stmt.setLong(3, currentTime);
                    stmt.executeUpdate();
                }
                context.sessionAttribute("flashes", List.of("Your message was recorded"));
            }
            context.redirect(ROUTE_HOME);
        });

        // // Display's a user's tweets.
        app.get("/{username}", context -> {
            String username = context.pathParam("username");

            // Get the profile user
            Map<String, Object> profileUser = queryDbOne(
                    "SELECT * FROM user WHERE username = ?",
                    username);

            if (profileUser == null) {
                context.status(404);
                return;
            }

            // Check if current user is following this profile user
            boolean followed = false;
            User currentUser = context.attribute("user");

            if (currentUser != null) {
                Integer whoId = currentUser.getUserId();
                Integer whomId = (Integer) profileUser.get("user_id");

                Map<String, Object> followCheck = queryDbOne(
                        "SELECT 1 FROM follower WHERE who_id = ? AND whom_id = ?",
                        whoId, whomId);

                followed = (followCheck != null);
            }

            // Get messages from this specific user
            String sql = "SELECT message.*, user.* FROM message, user " +
                    "WHERE user.user_id = message.author_id AND user.user_id = ? " +
                    "ORDER BY message.pub_date DESC LIMIT ?";
            List<Map<String, Object>> messages = queryDb(sql, profileUser.get("user_id"), PER_PAGE);
            hydrateMessages(messages);

            Map<String, Object> model = createModel(context);
            model.put("messages", messages);
            model.put("followed", followed);
            model.put("profile_user", profileUser);
            model.put("endpoint", "user_timeline");

            context.render("timeline.html", model);
        });

        // Removes the current user as follower of the given user.
        app.get("/{username}/unfollow", context -> {
            String username = context.pathParam("username");

            // Checks if user is logged in.
            if (context.attribute("user") == null) {
                context.status(401);
                return;
            }

            Integer whomId = getUserId(username);
            if (whomId == null) {
                context.status(404);
                return;
            }

            String sql = "DELETE FROM follower WHERE who_id = ? AND whom_id = ?";
            try (Connection db = connectDb(); var stmt = db.prepareStatement(sql)) {
                stmt.setObject(1, context.sessionAttribute("user_id"));
                stmt.setObject(2, whomId);
                stmt.executeUpdate();
            }

            context.sessionAttribute("flashes", List.of("You are no longer following \"" + username + "\""));
            context.redirect("/" + username);
        });

        return app;
    }
}