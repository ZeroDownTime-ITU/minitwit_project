package zerodowntime;

import io.javalin.Javalin;
import okhttp3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MinitwitTest {
    private static final int TEST_PORT = 7071;
    private static final String BASE_URL = "http://localhost:" + TEST_PORT;

    @TempDir
    Path tempDir;

    private Javalin app;
    private OkHttpClient client;

    @BeforeEach
    public void setUp() throws Exception {
        App.database = "jdbc:sqlite:" + tempDir.resolve("test.db");
        App.initDb();

        // Client with cookie support
        HashMap<String, List<Cookie>> cookies = new HashMap<>();
        client = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    public void saveFromResponse(HttpUrl u, List<Cookie> c) {
                        cookies.put(u.host(), c);
                    }

                    public List<Cookie> loadForRequest(HttpUrl u) {
                        return cookies.getOrDefault(u.host(), new ArrayList<>());
                    }
                })
                .followRedirects(true)
                .build();

        app = App.createApp().start(TEST_PORT);
    }

    @AfterEach
    public void tearDown() {
        if (app != null) {
            app.stop();
        }
    }

    // Helper methods
    private Response post(String path, String formData) throws IOException {
        RequestBody body = RequestBody.create(
                formData,
                MediaType.parse("application/x-www-form-urlencoded"));

        Request request = new Request.Builder()
                .url(BASE_URL + path)
                .post(body)
                .build();

        return client.newCall(request).execute();
    }

    private Response get(String path) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + path)
                .get()
                .build();

        return client.newCall(request).execute();
    }

    @Test
    public void testRegister() throws IOException {
        // Successful registration - verify by being able to login
        post("/register", "username=user1&email=u1@ex.com&password=abc&password2=abc").close();

        try (Response loginRes = post("/login", "username=user1&password=abc")) {
            String loginBody = loginRes.body().string();
            assertThat(loginBody).doesNotContain("Sign In");
            assertThat(loginBody).contains("MiniTwit");
        }

        // Duplicate username
        try (Response dupRes = post("/register", "username=user1&email=u2@ex.com&password=abc&password2=abc")) {
            String dupBody = dupRes.body().string();
            assertThat(dupBody).contains("The username is already taken");
            assertThat(dupBody).contains("Sign Up");
        }

        // Empty username
        try (Response res = post("/register", "username=&email=u3@ex.com&password=abc&password2=abc")) {
            assertThat(res.body().string()).contains("You have to enter a username");
        }

        // Empty password
        try (Response res = post("/register", "username=meh&email=u3@ex.com&password=&password2=")) {
            assertThat(res.body().string()).contains("You have to enter a password");
        }

        // Mismatched passwords
        try (Response res = post("/register", "username=meh&email=u3@ex.com&password=x&password2=y")) {
            assertThat(res.body().string()).contains("The two passwords do not match");
        }

        // Invalid email
        try (Response res = post("/register", "username=meh&email=broken&password=foo&password2=foo")) {
            assertThat(res.body().string()).contains("You have to enter a valid email address");
        }
    }

    @Test
    public void testLoginLogout() throws IOException {
        // Setup: Create a user
        post("/register", "username=user1&email=u1@ex.com&password=default&password2=default").close();

        // Successful Login
        try (Response loginRes = post("/login", "username=user1&password=default")) {
            String loginBody = loginRes.body().string();
            assertThat(loginBody).doesNotContain("Sign In");
            assertThat(loginBody).contains("MiniTwit");
        }

        // Logout
        try (Response logoutRes = get("/logout")) {
            assertThat(logoutRes.body().string()).contains("public timeline");
        }

        // After logout, redirected to public
        try (Response homeRes = get("/")) {
            assertThat(homeRes.body().string()).contains("public timeline");
        }

        // Wrong password
        try (Response res = post("/login", "username=user1&password=wrongpassword")) {
            assertThat(res.body().string())
                    .contains("Invalid password")
                    .contains("Sign In");
        }

        // Wrong username
        try (Response res = post("/login", "username=nonexistent&password=default")) {
            assertThat(res.body().string())
                    .contains("Invalid username")
                    .contains("Sign In");
        }
    }

    @Test
    public void testMessageRecording() throws IOException {
        // Setup: Register and login
        post("/register", "username=foo&email=f@ex.com&password=abc&password2=abc").close();
        post("/login", "username=foo&password=abc").close();

        // Record messages
        post("/add_message", "text=test message 1").close();
        post("/add_message", "text=<test message 2>").close();

        // Verify messages appear on home timeline
        try (Response homeRes = get("/")) {
            String homeBody = homeRes.body().string();
            assertThat(homeBody).contains("test message 1");
            assertThat(homeBody).contains("&lt;test message 2&gt;");
            assertThat(homeBody).doesNotContain("<test message 2>");
        }

        // Verify on profile page
        try (Response profileRes = get("/foo")) {
            String profileBody = profileRes.body().string();
            assertThat(profileBody).contains("test message 1");
            assertThat(profileBody).contains("&lt;test message 2&gt;");
        }

        // Verify on public timeline
        try (Response publicRes = get("/public")) {
            String publicBody = publicRes.body().string();
            assertThat(publicBody).contains("test message 1");
            assertThat(publicBody).contains("&lt;test message 2&gt;");
        }
    }

    @Test
    public void testTimelines() throws IOException {
        // Create foo user
        post("/register", "username=foo&email=f@ex.com&password=abc&password2=abc").close();
        post("/login", "username=foo&password=abc").close();
        post("/add_message", "text=the message by foo").close();
        get("/logout").close();

        // Create bar user
        post("/register", "username=bar&email=b@ex.com&password=abc&password2=abc").close();
        post("/login", "username=bar&password=abc").close();
        post("/add_message", "text=the message by bar").close();

        // Public timeline shows both
        try (Response publicRes = get("/public")) {
            String publicBody = publicRes.body().string();
            assertThat(publicBody).contains("the message by foo");
            assertThat(publicBody).contains("the message by bar");
        }

        // Bar's home shows only bar's message
        try (Response homeRes = get("/")) {
            String homeBody = homeRes.body().string();
            assertThat(homeBody).contains("the message by bar");
            assertThat(homeBody).doesNotContain("the message by foo");
        }

        // Follow foo
        get("/foo/follow").close();

        // Bar's home now shows both
        try (Response homeRes = get("/")) {
            String homeBody = homeRes.body().string();
            assertThat(homeBody).contains("the message by foo");
            assertThat(homeBody).contains("the message by bar");
        }

        // Profile pages show only that user's messages
        try (Response barRes = get("/bar")) {
            String body = barRes.body().string();
            assertThat(body).contains("the message by bar");
            assertThat(body).doesNotContain("the message by foo");
        }

        try (Response fooRes = get("/foo")) {
            String body = fooRes.body().string();
            assertThat(body).contains("the message by foo");
            assertThat(body).doesNotContain("the message by bar");
        }

        // Unfollow foo
        get("/foo/unfollow").close();

        // Bar's home shows only bar's message again
        try (Response homeRes = get("/")) {
            String homeBody = homeRes.body().string();
            assertThat(homeBody).contains("the message by bar");
            assertThat(homeBody).doesNotContain("the message by foo");
        }
    }

    @Test
    public void testFollowUnfollowButtons() throws IOException {
        // Create users
        post("/register", "username=alice&email=a@ex.com&password=abc&password2=abc").close();
        get("/logout").close();

        post("/register", "username=bob&email=b@ex.com&password=abc&password2=abc").close();
        post("/login", "username=bob&password=abc").close();

        // Bob sees follow button on Alice's profile
        try (Response res = get("/alice")) {
            assertThat(res.body().string()).contains("/alice/follow");
        }

        // Bob follows Alice
        get("/alice/follow").close();

        // Bob sees unfollow button
        try (Response res = get("/alice")) {
            assertThat(res.body().string()).contains("/alice/unfollow");
        }

        // Bob unfollows Alice
        get("/alice/unfollow").close();

        // Follow button is back
        try (Response res = get("/alice")) {
            assertThat(res.body().string()).contains("/alice/follow");
        }
    }

    @Test
    public void testUnauthorizedAccess() throws IOException {
        // Try to add message without login
        try (Response res = post("/add_message", "text=unauthorized")) {
            assertThat(res.code()).isEqualTo(401);
        }

        // Try to follow without login
        try (Response res = get("/someuser/follow")) {
            assertThat(res.code()).isEqualTo(401);
        }

        // Try to unfollow without login
        try (Response res = get("/someuser/unfollow")) {
            assertThat(res.code()).isEqualTo(401);
        }

        // Home redirects to public when not logged in
        try (Response res = get("/")) {
            assertThat(res.body().string()).contains("public timeline");
        }
    }

    @Test
    public void testNonexistentUserProfile() throws IOException {
        // Login first
        post("/register", "username=user1&email=u1@ex.com&password=abc&password2=abc").close();
        post("/login", "username=user1&password=abc").close();

        // Non-existent profile returns 404
        try (Response res = get("/nonexistentuser")) {
            assertThat(res.code()).isEqualTo(404);
        }

        // Can't follow non-existent user
        try (Response res = get("/nonexistentuser/follow")) {
            assertThat(res.code()).isEqualTo(404);
        }

        // Can't unfollow non-existent user
        try (Response res = get("/nonexistentuser/unfollow")) {
            assertThat(res.code()).isEqualTo(404);
        }
    }
}