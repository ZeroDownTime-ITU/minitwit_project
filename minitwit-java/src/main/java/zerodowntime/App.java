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

import org.mindrot.jbcrypt.BCrypt;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.rendering.template.JavalinPebble;

public class App {

    // Configuration
    private static final String DB_FILE_PATH = "data/minitwit-java.db";
    public static final String DATABASE = "jdbc:sqlite:" + DB_FILE_PATH;

    public static final int PER_PAGE = 30;
    public static final boolean DEBUG = true;
    public static final String SECRET_KEY = "development key";

    private static final String ROUTE_HOME = "/";
    private static final String ROUTE_PUBLIC = "/public";
    private static final String ROUTE_LOGIN = "/login";

    // Returns a new connection to the database
    public static Connection connectDb() throws SQLException {
        return DriverManager.getConnection(DATABASE);
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

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> queryDb(String query, Object... args) {
        return (List<Map<String, Object>>) queryDbInternal(query, false, args);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> queryDbOne(String query, Object... args) {
        return (Map<String, Object>) queryDbInternal(query, true, args);
    }

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

    // Format a timestamp for display.
    public static String formatDatetime(long timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        ZonedDateTime dt = instant.atZone(ZoneOffset.UTC);
        return dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd @ HH:mm"));
    }

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
        Path dbPath = Paths.get(DB_FILE_PATH);

        if (Files.exists(dbPath)) {
            System.out.println("Database already exists at: " + DB_FILE_PATH);
        } else {
            System.out.println("Database not found. Initializing...");
            System.out.println("Creating database at: " + DB_FILE_PATH);
            initDb();
            System.out.println("Database initialized successfully.");
        }

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/static";
                staticFiles.directory = "/static";
            });
            config.fileRenderer(new JavalinPebble());
        }).start(7070); // Port 7070

        // Lookup the current user so that we know he's there
        app.before(context -> {
            // Load user from session
            Integer userId = context.sessionAttribute("user_id");
            if (userId != null) {
                Map<String, Object> user = queryDbOne(
                        "SELECT * FROM user WHERE user_id = ?",
                        userId);
                context.attribute("user", user);
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

        // ---------------- Routes/endpoints below ----------------

        // Shows a users timeline or if no user is logged in it will
        // redirect to the public timeline. This shows the user's
        // messages as well as all the messages of followed users.
        app.get(ROUTE_HOME, context -> {
            System.out.println("We got a visitor from: " + context.ip());

            Map<String, Object> user = context.attribute("user");
            if (user == null) {
                context.redirect(ROUTE_PUBLIC);
                return;
            }

            int userId = (int) user.get("user_id");
            String sql = "SELECT message.*, user.* FROM message, user " +
                    "WHERE message.flagged = 0 AND message.author_id = user.user_id AND (" +
                    "user.user_id = ? OR " +
                    "user.user_id IN (SELECT whom_id FROM follower WHERE who_id = ?)) " +
                    "ORDER BY message.pub_date DESC LIMIT ?";

            List<Map<String, Object>> messages = queryDb(sql, userId, userId, PER_PAGE);
            hydrateMessages(messages);

            Map<String, Object> model = createModel(context);
            model.put("messages", messages);
            model.put("endpoint", "timeline");

            context.render("timeline.html", model);
        });

        // Displays the latest messages of all users.
        app.get(ROUTE_PUBLIC, context -> {
            String sql = "SELECT message.*, user.* FROM message, user " +
                    "WHERE message.flagged = 0 AND message.author_id = user.user_id " +
                    "ORDER BY message.pub_date DESC LIMIT ?";

            List<Map<String, Object>> messages = queryDb(sql, PER_PAGE);
            hydrateMessages(messages);

            Map<String, Object> model = createModel(context);
            model.put("messages", messages);
            model.put("endpoint", "public_timeline");

            context.render("timeline.html", model);
        });

        // Get request for logging the user in.
        app.get(ROUTE_LOGIN, context -> {
            if (context.attribute("user") != null) {
                context.redirect(ROUTE_HOME);
                return;
            }

            Map<String, Object> model = createModel(context);
            model.put("error", "");
            context.render("login.html", model);
        });

        // Post (submit) request for logging the user in.
        app.post(ROUTE_LOGIN, context -> {
            if (context.attribute("user") != null) {
                context.redirect(ROUTE_HOME);
                return;
            }

            String username = context.formParam("username");
            String password = context.formParam("password");
            String error = null;

            Map<String, Object> user = queryDbOne(
                    "SELECT * FROM user WHERE username = ?",
                    username);

            if (user == null) {
                error = "Invalid username";
            } else if (!BCrypt.checkpw(password, (String) user.get("pw_hash"))) {
                error = "Invalid password";
            } else {
                context.sessionAttribute("flashes", List.of("You were logged in"));
                context.sessionAttribute("user_id", user.get("user_id"));
                context.redirect(ROUTE_HOME);
                return;
            }

            Map<String, Object> model = createModel(context);
            model.put("error", error);
            context.render("login.html", model);
        });

        // Get request for registing the user.
        app.get("/register", context -> {
            if (context.attribute("user") != null) {
                context.redirect(ROUTE_HOME);
                return;
            }

            Map<String, Object> model = createModel(context);
            model.put("error", "");
            context.render("register.html", model);
        });

        // Post (submit) request for registering the user.
        app.post("/register", context -> {
            String username = context.formParam("username");
            String email = context.formParam("email");
            String password = context.formParam("password");
            String passwordConfirm = context.formParam("password2");
            String error = null;

            if (username == null || username.isBlank()) {
                error = "You have to enter a username";
            } else if (email == null || !email.contains("@")) {
                error = "You have to enter a valid email address";
            } else if (password == null || password.isBlank()) {
                error = "You have to enter a password";
            } else if (!password.equals(passwordConfirm)) {
                error = "The two passwords do not match";
            } else if (queryDbOne("SELECT user_id FROM user WHERE username = ?", username) != null) {
                error = "The username is already taken";
            } else {
                String pwHash = BCrypt.hashpw(password, BCrypt.gensalt()); // Hash the password

                String sql = "INSERT INTO user (username, email, pw_hash) VALUES (?, ?, ?)";
                try (Connection db = connectDb(); var stmt = db.prepareStatement(sql)) {
                    stmt.setString(1, username);
                    stmt.setString(2, email);
                    stmt.setString(3, pwHash);
                    stmt.executeUpdate();
                }

                context.sessionAttribute("flashes", List.of("You were successfully registered and can login now"));
                context.redirect(ROUTE_LOGIN);
                return;
            }

            Map<String, Object> model = createModel(context);
            model.put("error", error);
            context.render("register.html", model);
        });

        // Logout
        app.get("/logout", context -> {
            context.sessionAttribute("user_id", null);
            context.sessionAttribute("flashes", List.of("You were logged out"));
            context.redirect(ROUTE_PUBLIC);
        });

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

        // Adds the current user as follower of the given user.
        app.get("/{username}/follow", context -> {
            String username = context.pathParam("username");

            if (context.attribute("user") == null) {
                context.status(401);
                return;
            }

            Integer whomId = getUserId(username);
            if (whomId == null) {
                context.status(404);
                return;
            }

            String sql = "INSERT INTO follower (who_id, whom_id) VALUES (?, ?)";
            try (Connection db = connectDb(); var stmt = db.prepareStatement(sql)) {
                stmt.setObject(1, context.sessionAttribute("user_id"));
                stmt.setObject(2, whomId);
                stmt.executeUpdate();
            }

            context.sessionAttribute("flashes", List.of("You are now following \"" + username + "\""));
            context.redirect("/" + username);
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
            Map<String, Object> currentUser = context.attribute("user");

            if (currentUser != null) {
                Integer whoId = (Integer) currentUser.get("user_id");
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
    }
}