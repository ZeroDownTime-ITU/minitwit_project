package zerodowntime.constants;

public class AppConstants {
    // Database & Pagination
    public static final int PER_PAGE = 30;
    public static final String DB_PATH = "data/minitwit-java.db";

    // Web UI Routes
    public static class Web {
        public static final String HOME = "/";
        public static final String PUBLIC = "/public";
        public static final String LOGIN = "/login";
        public static final String LOGOUT = "/logout";
        public static final String REGISTER = "/register";
        public static final String ADD_MESSAGE = "/add_message";
        public static final String USER_PROFILE = "/{username}";
        public static final String FOLLOW = "/{username}/follow";
        public static final String UNFOLLOW = "/{username}/unfollow";
    }

    // Simulator/API Routes
    public static class Api {
        public static final String LATEST = "/latest";
        public static final String REGISTER = "/register"; // Or "/api/register" if you prefix
        public static final String MSGS = "/msgs";
        public static final String MSGS_USER = "/msgs/{username}";
        public static final String FLLWS_USER = "/fllws/{username}";
    }
}