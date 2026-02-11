package zerodowntime;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinPebble;

public class App {

    // Configuration - Updated to check for Docker Environment Variable
    private static final String DB_FILE_PATH = System.getenv("DB_FILE") != null 
                                               ? System.getenv("DB_FILE") 
                                               : "data/minitwit-java.db";
    
    public static final String DATABASE = "jdbc:sqlite:" + DB_FILE_PATH;

    public static final int PER_PAGE = 30;
    public static final boolean DEBUG = true;
    public static final String SECRET_KEY = "development key";

    // Returns a new connection to the database
    public static Connection connectDb() throws SQLException {
        return DriverManager.getConnection(DATABASE);
    }

    public static Integer getUserId(String username) {
        // Placeholder for user ID lookup logic
        return 23;
    }
    
    // Creates the database tables
    public static void initDb() throws Exception {
        InputStream inputStream = App.class.getResourceAsStream("/schema.sql");
        if (inputStream == null) {
            throw new RuntimeException("Schema file not found!");
        }
        String sql = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        try (Connection db = connectDb(); Statement stmt = db.createStatement()) {
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

    private static Object queryDbInternal(String query, boolean one, Object... args) {
        var results = new ArrayList<Map<String, Object>>();

        try (var db = connectDb(); var stmt = db.prepareStatement(query)) {
            for (int i = 0; i < args.length; i++) {
                stmt.setObject(i + 1, args[i]);
            }

            var rs = stmt.executeQuery();
            var meta = rs.getMetaData();

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

        if (one) {
            return results.isEmpty() ? null : results.get(0);
        }
        return results;
    }

    public static void main(String[] args) throws Exception {
        Path dbPath = Paths.get(DB_FILE_PATH);

        // Ensure the directory exists if we're using a subfolder
        if (dbPath.getParent() != null) {
            Files.createDirectories(dbPath.getParent());
        }

        if (Files.exists(dbPath)) {
            System.out.println("Database already exists at: " + DB_FILE_PATH);
        } else {
            System.out.println("Database not found. Initializing...");
            initDb();
            System.out.println("Database initialized successfully.");
        }

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/static");
            config.fileRenderer(new JavalinPebble());
        }).start(7070);

        app.before(context -> {
            Connection db = connectDb();
            context.attribute("db", db);

            Integer userId = context.sessionAttribute("user_id");
            if (userId != null) {
                Map<String, Object> user = queryDbOne(
                    "SELECT * FROM user WHERE user_id = ?", 
                    userId
                );
                context.attribute("user", user);
            } else {
                context.attribute("user", null);
            }
        });

        app.after(context -> {
            Connection db = context.attribute("db");
            if (db != null) {
                db.close();
            }
        });

        // ---------------- Routes/endpoints ----------------

        app.get("/", context -> {
            context.redirect("/timeline");
        });

        app.get("/timeline", context -> {
            Connection db = context.attribute("db");
            Map<String, Object> user = context.attribute("user");

            if (user == null) {
                context.redirect("/public");
                return;
            }
            int userId = (int) user.get("user_id");

            String sql = "SELECT message.*, user.* FROM message, user " +
                "WHERE message.flagged = 0 AND message.author_id = user.user_id AND (" +
                "user.user_id = ? OR " +
                "user.user_id IN (SELECT whom_id FROM follower " +
                "WHERE who_id = ?)) " +
                "ORDER BY message.pub_date DESC LIMIT ?";
            List<Map<String, Object>> messages = queryDb(sql, userId, userId, PER_PAGE);
            
            Map<String, Object> model = new HashMap<>();
            model.put("messages", messages);
            model.put("endpoint", "user_timeline");
            model.put("g", Map.of("user", user));

            context.render("timeline.html", model);
        });

        app.get("/public", context -> {
            String sql = "SELECT message.*, user.* FROM message, user " +
                "WHERE message.flagged = 0 AND message.author_id = user.user_id " +
                "ORDER BY message.pub_date DESC LIMIT ?";
            List<Map<String, Object>> messages = queryDb(sql, PER_PAGE);

            Map<String, Object> model = new HashMap<>();
            model.put("messages", messages);
            model.put("endpoint", "public_timeline");
            model.put("g", Map.of("user", context.attribute("user") != null ? context.attribute("user") : ""));

            context.render("timeline.html", model);
        });

        // Displays a user's tweets.
        app.get("/{username}", context -> {
            String username = context.pathParam("username");
            
            // 1. Fetch the profile user
            Map<String, Object> profileUser = queryDbOne(
                "SELECT * FROM user WHERE username = ?", 
                username
            );

            if (profileUser == null) {
                context.status(404);
                return;
            }

            // 2. Check if the current logged-in user follows this profile
            boolean followed = false;
            Map<String, Object> currentUser = context.attribute("user");
            
            if (currentUser != null) {
                Map<String, Object> followCheck = queryDbOne(
                    "SELECT 1 FROM follower WHERE who_id = ? AND whom_id = ?",
                    currentUser.get("user_id"), 
                    profileUser.get("user_id")
                );
                followed = (followCheck != null);
            }

            // 3. Fetch the messages for this specific user
            String sql = "SELECT message.*, user.* FROM message, user WHERE " +
                        "user.user_id = message.author_id AND user.user_id = ? " +
                        "ORDER BY message.pub_date DESC LIMIT ?";
            List<Map<String, Object>> messages = queryDb(sql, profileUser.get("user_id"), PER_PAGE);

            // 4. Build the model for Pebble
            Map<String, Object> model = new HashMap<>();
            model.put("messages", messages);
            model.put("followed", followed);
            model.put("profile_user", profileUser);
            model.put("endpoint", "user_timeline");

            // Global 'g' object mimic for template compatibility
            Map<String, Object> g = new HashMap<>();
            g.put("user", currentUser);
            model.put("g", g);

            context.render("timeline.html", model);
        });

        app.get("/login", context -> {
            context.render("login.html", Map.of("error", ""));
        });

        app.post("/login", context -> {
            String username = context.formParam("username");
            String password = context.formParam("password");
            
            Map<String, Object> user = queryDbOne("SELECT * FROM user WHERE username = ?", username);

            if (user != null && BCrypt.checkpw(password, (String) user.get("pw_hash"))) {
                context.sessionAttribute("user_id", user.get("user_id"));
                context.redirect("/timeline");
            } else {
                context.render("login.html", Map.of("error", "Invalid login"));
            }
        });

        app.get("/logout", context -> {
            context.sessionAttribute("user_id", null);
            context.redirect("/public");
        });
    }
}