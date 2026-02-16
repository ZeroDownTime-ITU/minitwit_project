package zerodowntime;

import io.javalin.Javalin;
import okhttp3.*;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import zerodowntime.constants.AppConstants.PublicApi;
import zerodowntime.dto.web.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class MinitwitTest {
    private static final int TEST_PORT = 7071;
    private static final String BASE_URL = "http://localhost:" + TEST_PORT;

    @TempDir
    Path tempDir;

    private Javalin app;
    private OkHttpClient client;
    private TestHelper http;

    @BeforeEach
    public void setUp() {
        String testDbUrl = "jdbc:sqlite:" + tempDir.resolve("test.db");
        Jdbi testJdbi = DatabaseManager.createDatabase(testDbUrl);

        app = App.createApp(testJdbi).start(TEST_PORT);
        client = createTestClient();
        http = new TestHelper(client, BASE_URL);
    }

    @AfterEach
    public void tearDown() {
        if (app != null) {
            app.stop();
        }
    }

    private OkHttpClient createTestClient() {
        HashMap<String, List<Cookie>> cookies = new HashMap<>();
        return new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl u, List<Cookie> c) {
                        cookies.put(u.host(), c);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl u) {
                        return cookies.getOrDefault(u.host(), new ArrayList<>());
                    }
                })
                .followRedirects(true)
                .build();
    }

    @Test
    public void testRegister() throws IOException {
        // Use actual DTOs!
        RegisterRequest req = new RegisterRequest("user1", "u1@ex.com", "abc", "abc");

        try (Response res = http.postJson(PublicApi.REGISTER, req)) {
            assertThat(res.code()).isEqualTo(200);
        }

        // Verify can login
        LoginRequest loginReq = new LoginRequest("user1", "abc");

        try (Response loginRes = http.postJson(PublicApi.LOGIN, loginReq)) {
            assertThat(loginRes.code()).isEqualTo(200);

            // Parse JSON response to DTO
            UserDto user = http.readJson(loginRes.body().string(), UserDto.class);
            assertThat(user.username()).isEqualTo("user1");
            assertThat(user.email()).isEqualTo("u1@ex.com");
        }

        // Duplicate username
        try (Response dupRes = http.postJson(PublicApi.REGISTER, req)) {
            assertThat(dupRes.code()).isEqualTo(401);
            assertThat(dupRes.body().string()).contains("already taken");
        }

        // Empty username
        RegisterRequest emptyUser = new RegisterRequest("", "u2@ex.com", "abc", "abc");

        try (Response res = http.postJson(PublicApi.REGISTER, emptyUser)) {
            assertThat(res.code()).isEqualTo(401);
            assertThat(res.body().string()).contains("username");
        }

        // Mismatched passwords
        RegisterRequest mismatch = new RegisterRequest("user2", "u2@ex.com", "abc", "xyz");

        try (Response res = http.postJson(PublicApi.REGISTER, mismatch)) {
            assertThat(res.code()).isEqualTo(401);
            assertThat(res.body().string()).contains("do not match");
        }
    }

    @Test
    public void testLoginLogout() throws IOException {
        // Register using DTO
        RegisterRequest registerReq = new RegisterRequest("user1", "u1@ex.com", "default", "default");
        http.postJson(PublicApi.REGISTER, registerReq).close();

        // Login using DTO
        LoginRequest loginReq = new LoginRequest("user1", "default");

        try (Response loginRes = http.postJson(PublicApi.LOGIN, loginReq)) {
            assertThat(loginRes.code()).isEqualTo(200);

            UserDto user = http.readJson(loginRes.body().string(), UserDto.class);
            assertThat(user.username()).isEqualTo("user1");
        }

        // Check session
        try (Response sessionRes = http.get(PublicApi.SESSION)) {
            assertThat(sessionRes.code()).isEqualTo(200);

            UserDto user = http.readJson(sessionRes.body().string(), UserDto.class);
            assertThat(user.username()).isEqualTo("user1");
        }

        // Logout (empty object)
        try (Response logoutRes = http.postJson(PublicApi.LOGOUT, Map.of())) {
            assertThat(logoutRes.code()).isEqualTo(200);
        }

        // Session should be gone
        try (Response sessionRes = http.get(PublicApi.SESSION)) {
            assertThat(sessionRes.code()).isEqualTo(401);
        }

        // Wrong password
        try (Response res = http.postJson(PublicApi.LOGIN, new LoginRequest("user1", "wrongpassword"))) {
            assertThat(res.code()).isEqualTo(401);
        }

        // Wrong username
        try (Response res = http.postJson(PublicApi.LOGIN, new LoginRequest("nonexistent", "default"))) {
            assertThat(res.code()).isEqualTo(401);
        }
    }

    @Test
    public void testMessageRecording() throws IOException {
        registerAndLogin("foo", "f@ex.com", "abc");

        // Post messages using DTOs
        http.postJson(PublicApi.POSTMESSAGE, new MessageRequest("test message 1")).close();
        http.postJson(PublicApi.POSTMESSAGE, new MessageRequest("<test message 2>")).close();

        // Verify messages on timeline
        try (Response timelineRes = http.get(PublicApi.USER_TIMELINE)) {
            assertThat(timelineRes.code()).isEqualTo(200);
            String body = timelineRes.body().string();
            assertThat(body).contains("test message 1");
            assertThat(body).contains("<test message 2>");
        }

        // Verify on public timeline
        try (Response publicRes = http.get(PublicApi.PUBLIC_TIMELINE)) {
            assertThat(publicRes.code()).isEqualTo(200);
            String body = publicRes.body().string();
            assertThat(body).contains("test message 1");
            assertThat(body).contains("<test message 2>");
        }
    }

    @Test
    public void testTimelines() throws IOException {
        // Create foo user
        registerAndLogin("foo", "f@ex.com", "abc");
        http.postJson(PublicApi.POSTMESSAGE, new MessageRequest("the message by foo")).close();
        http.postJson(PublicApi.LOGOUT, Map.of()).close();

        // Create bar user
        registerAndLogin("bar", "b@ex.com", "abc");
        http.postJson(PublicApi.POSTMESSAGE, new MessageRequest("the message by bar")).close();

        // Public timeline shows both
        try (Response publicRes = http.get(PublicApi.PUBLIC_TIMELINE)) {
            String body = publicRes.body().string();
            assertThat(body).contains("the message by foo");
            assertThat(body).contains("the message by bar");
        }

        // Bar's home shows only bar's message
        try (Response homeRes = http.get(PublicApi.USER_TIMELINE)) {
            String body = homeRes.body().string();
            assertThat(body).contains("the message by bar");
            assertThat(body).doesNotContain("the message by foo");
        }

        // Follow foo
        try (Response followRes = http.post(PublicApi.FOLLOW, "foo")) {
            assertThat(followRes.code()).isEqualTo(204);
        }

        // Bar's home now shows both
        try (Response homeRes = http.get(PublicApi.USER_TIMELINE)) {
            String body = homeRes.body().string();
            assertThat(body).contains("the message by foo");
            assertThat(body).contains("the message by bar");
        }

        // Unfollow foo
        try (Response unfollowRes = http.post(PublicApi.UNFOLLOW, "foo")) {
            assertThat(unfollowRes.code()).isIn(200, 204);
        }

        // Bar's home shows only bar's message again
        try (Response homeRes = http.get(PublicApi.USER_TIMELINE)) {
            String body = homeRes.body().string();
            assertThat(body).contains("the message by bar");
            assertThat(body).doesNotContain("the message by foo");
        }
    }

    @Test
    public void testUnauthorizedAccess() throws IOException {
        try (Response res = http.postJson(PublicApi.POSTMESSAGE, new MessageRequest("unauthorized"))) {
            assertThat(res.code()).isEqualTo(401);
        }

        try (Response res = http.post(PublicApi.FOLLOW, "someuser")) {
            assertThat(res.code()).isEqualTo(401);
        }

        try (Response res = http.get(PublicApi.USER_TIMELINE)) {
            assertThat(res.code()).isEqualTo(401);
        }
    }

    @Test
    public void testNonexistentUserProfile() throws IOException {
        registerAndLogin("user1", "u1@ex.com", "abc");

        try (Response res = http.get(PublicApi.USER_PROFILE + "/nonexistentuser")) {
            assertThat(res.code()).isEqualTo(404);
        }

        try (Response res = http.post(PublicApi.FOLLOW, "nonexistentuser")) {
            assertThat(res.code()).isEqualTo(404);
        }
    }

    // Helper method
    private void registerAndLogin(String username, String email, String password) throws IOException {
        http.postJson(PublicApi.REGISTER,
                new RegisterRequest(username, email, password, password)).close();
        http.postJson(PublicApi.LOGIN,
                new LoginRequest(username, password)).close();
    }
}