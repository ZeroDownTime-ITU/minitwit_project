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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinPebble;

public class App {

    // Configuration
    private static final String DB_FILE_PATH = "data/minitwit-java.db";
    public static final String DATABASE = "jdbc:sqlite:" + DB_FILE_PATH;

    public static final int PER_PAGE = 30;
    public static final boolean DEBUG = true;
    public static final String SECRET_KEY = "development key";

    // Returns a new connection to the database
    public static Connection connectDb() throws SQLException {
        return DriverManager.getConnection(DATABASE);
    }

    public static Integer getUserId(String username) {
        // Mathias code here. Dependency for Kasper 
        return 23; // random user id I have put here for testing. 
    }
    
    // Creates the database tables
    public static void initDb() throws Exception {
        InputStream inputStream = App.class.getResourceAsStream("/schema.sql");
        String sql = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        try (Connection db = connectDb(); Statement stmt = db.createStatement()) { // Auto-close when done
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

        // Returns first result if one == true, else return full list
        if (one) {
            return results.isEmpty() ? null : results.get(0);
        }
        return results;
    }

    public static void main(String[] args) throws Exception {
        // if (args.length > 0 && args[0].equals("init")) {
        // initDb();
        // System.out.println("Database initialized. Exiting.");
        // System.out.println("DATABASE URL: " + DATABASE);

        // return;
        // }
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
            config.staticFiles.add("/static");
            config.fileRenderer(new JavalinPebble());
        }).start(7070); // Port 7070

        // Make sure we are connected to the database each request and look
        // -up the current user so that we know he's there
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

        // Closes the database again at the end of the request.
        app.after(context -> {
            Connection db = context.attribute("db");
            if (db != null) {
                db.close();
            }
        });

        // ---------------- Routes/endpoints below ----------------

        // Redirect to timeline if empty route
        app.get("/", context -> {
            context.redirect("/timeline");
        });

        // Shows a users timeline or if no user is logged in it will
        // redirect to the public timeline. This timeline shows the user's
        // messages as well as all the messages of followed users.
        app.get("/timeline", context -> {
            System.out.println("We got a visitor from: " + context.ip());

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

            Map<String, Object> g = new HashMap<>();
            g.put("user", context.attribute("user"));
            model.put("g", g);

            context.render("timeline.html", model);
        });

        // Displays the latest messages of all users.
        app.get("/public", context -> {
            String sql = "SELECT message.*, user.* FROM message, user " +
                "WHERE message.flagged = 0 AND message.author_id = user.user_id " +
                "ORDER BY message.pub_date DESC LIMIT ?";
            List<Map<String, Object>> messages = queryDb(sql, PER_PAGE);

            Map<String, Object> model = new HashMap<>();
            model.put("messages", messages);
            model.put("endpoint", "public_timeline");

            Map<String, Object> g = new HashMap<>();
            g.put("user", context.attribute("user"));
            model.put("g", g);

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

        //TODO: app.get username follow

        //TODO: app.get username unfollow

        //TODO: app.get add message

        

        // Get request for logging the user in.
        app.get("/login", context -> {
            if (context.attribute("user") != null) {
                context.redirect("/timeline");
                return;
            }
            context.render("login.html", Map.of("error", ""));
        });

        // Post (submit) request for logging the user in.
        app.post("/login", context -> {
            if (context.attribute("user") != null) {
                context.redirect("/timeline");
                return;
            }
            
            String username = context.formParam("username");
            String password = context.formParam("password");
            String error = null;

            Map<String, Object> user = queryDbOne(
                "SELECT * FROM user WHERE username = ?", 
                username
            );

            if (user == null) {
                error = "Invalid username";
            }
            else if (!BCrypt.checkpw(password, (String) user.get("pw_hash"))) {
                error = "Invalid password";
            }
            else {
                context.sessionAttribute("flashes", List.of("You were logged in"));
                context.sessionAttribute("user_id", user.get("user_id"));
                context.redirect("/timeline");
                return;
            }

            context.render("login.html", Map.of("error", error));
        });

        // Get request for registing the user.
        app.get("/register", context -> {
            if (context.attribute("user") != null) {
                context.redirect("/timeline");
                return;
            }
            context.render("register.html", Map.of("error", ""));
        });

        // Adds the current user as follower of the given user.
        app.get("/{username}/follow", context -> {
            String username = context.pathParam("username");
            
            // Is the user logged in? 
            if (context.attribute("user") == null) {
                context.status(401);
                return;
            }
            
            // Get the ID of the user we wish to follow. 
            Integer whomId = getUserId(username);
            if (whomId == null) {
                context.status(404);
                return;
            }
            
            // Make sql statement to add the whom ID to our current users follow value
            String sql = "INSERT INTO follower (who_id, whom_id) VALUES (?, ?)"; 
            try (Connection db = connectDb(); var stmt = db.prepareStatement(sql)) {
                stmt.setObject(1, context.sessionAttribute("user_id"));
                stmt.setObject(2, whomId);
                stmt.executeUpdate();
            }

            context.sessionAttribute("flashes", List.of("You are now following \"" + username + "\""));
            context.redirect("/" + username);
        });

        // Removes the current user as follower of the given user.
        app.get("/{username}/unfollow", context ->{
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

            // Make sql statement
            String sql =  "DELETE FROM follower WHERE who_id = ? AND whom_id = ?"; 
            try (Connection db = connectDb(); var stmt = db.prepareStatement(sql)){
                stmt.setObject(1, context.sessionAttribute("user_id"));
                stmt.setObject(2, whomId);
                stmt.executeUpdate();
            }

            context.sessionAttribute("flashes", List.of("You are no longer following \"" + username + "\""));
            context.redirect("/" + username);  

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
                // Hash the password
                String pwHash = BCrypt.hashpw(password, BCrypt.gensalt());

                try (Connection db = connectDb()) {
                    var stmt = db.prepareStatement("INSERT INTO user (username, email, pw_hash) VALUES (?, ?, ?)");
                    stmt.setString(1, username);
                    stmt.setString(2, email);
                    stmt.setString(3, pwHash);
                    stmt.executeUpdate();
                }

                context.sessionAttribute("flashes", List.of("You were successfully registered and can login now"));
                context.redirect("/login");
                return;
            }

            context.render("register.html", Map.of("error", error));
        });

        app.get("/logout", context -> {
            context.sessionAttribute("flash", "You were logged out");
            context.sessionAttribute("user_id", null);
            context.redirect("/public");

            String flash = context.sessionAttribute("flash");
            context.sessionAttribute(flash, null);
        });
    }
}