package zerodowntime.constants;

public class AppConstants {
    public static final int PER_PAGE = 30;
    public static final String DB_PATH = "data/minitwit-java.db";

    /**
     * Simulator API: Strictly follows the Minitwit Simulator Specification.
     * These usually require the 'Authorization' header and specific JSON formats.
     */
    public static class SimulatorApi {
        public static final String LATEST = "/api/latest";
        public static final String REGISTER = "/api/register";
        public static final String MSGS = "/api/msgs";
        public static final String MSGS_USER = "/api/msgs/{username}";
        public static final String FLLWS_USER = "/api/fllws/{username}";
    }

    /**
     * Frontend API: Endpoints for your Svelte frontend.
     * These handle things like the public timeline, user profiles, and session
     * checks.
     */
    public static class PublicApi {
        public static final String PUBLIC_TIMELINE = "/api/public";
        public static final String USER_TIMELINE = "/api/user/{username}";
        public static final String FOLLOW = "/{username}/follow";
        public static final String UNFOLLOW = "/{username}/unfollow";
        public static final String USER_FOLLOWING = "/api/user/{username}/following";
        // Authentication for Svelte (not the simulator)
        public static final String LOGIN = "/api/auth/login";
        public static final String LOGOUT = "/api/auth/logout";
        public static final String REGISTER = "/api/auth/register";
        public static final String SESSION = "/api/auth/session";
    }
}