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
        public static final String PUBLIC_TIMELINE = "/web/public-timeline";
        public static final String USER_TIMELINE = "/web/user-timeline";
        public static final String USER_PROFILE = "/web/user/{username}";
        public static final String FOLLOW = "/web/follow/{username}";
        public static final String UNFOLLOW = "/web/unfollow/{username}";
        public static final String USER_FOLLOWING = "/web/user/{username}/following";
        public static final String POSTMESSAGE = "/web/add-message";

        public static final String LOGIN = "/web/auth/login";
        public static final String LOGOUT = "/web/auth/logout";
        public static final String REGISTER = "/web/auth/register";
        public static final String SESSION = "/web/auth/session";
    }
}