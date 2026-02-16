package zerodowntime.constants;

public class AppConstants {
    public static final int PER_PAGE = 30;
    public static final String DB_PATH = "data/minitwit-java.db";

    /**
     * Simulator API: Strictly follows the Minitwit Simulator Specification.
     * These use the 'Authorization' header.
     */
    public static class SimulatorApi {
        public static final String LATEST = "/api/latest";
        public static final String REGISTER = "/api/register";
        public static final String MSGS = "/api/msgs";
        public static final String MSGS_USER = "/api/msgs/{username}";
        public static final String FLLWS_USER = "/api/fllws/{username}";
    }

    /**
     * Frontend API: Endpoints for Svelte frontend.
     * These use session-based authentication.
     */
    public static class PublicApi {
        public static final String PUBLIC_TIMELINE = "/api/public-timeline";
        public static final String USER_TIMELINE = "/api/user-timeline";
        public static final String USER_PROFILE = "/api/user/{username}";
        public static final String FOLLOW = "/api/follow/{username}";
        public static final String UNFOLLOW = "/api/unfollow/{username}";
        public static final String USER_FOLLOWING = "/api/user/{username}/following";
        public static final String POSTMESSAGE = "/api/add-message";

        public static final String LOGIN = "/api/auth/login";
        public static final String LOGOUT = "/api/auth/logout";
        public static final String REGISTER = "/api/auth/register";
        public static final String SESSION = "/api/auth/session";
    }
}