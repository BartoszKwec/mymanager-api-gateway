package pl.kwec.mymanagerapigateway.config;

public final class TokenConstants {

    private TokenConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_ROLE = "role";
    public static final String CLAIM_EMAIL = "sub";

    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_EMAIL = "X-User-Email";

    public static final String ROLE_PREFIX = "ROLE_";
    
    public static final String PUBLIC_PATH_PATTERN = "/auth-service/auth/**";
    public static final String CORS_ALLOWED_ORIGIN = "http://localhost:5173";
    public static final String CORS_ALLOWED_HEADER = "*";
    public static final String CORS_ALLOWED_METHOD = "*";

    public static final String AUTH_SERVICE_ROUTE_NAME = "auth_service";
    public static final String AUTH_SERVICE_PATH_PATTERN = "/auth-service/auth/**";
    public static final String AUTH_SERVICE_REWRITE_PATH = "/auth-service/auth/(?<remaining>.*)";
    public static final String AUTH_SERVICE_REWRITE_REPLACEMENT = "/auth/${remaining}";
    public static final String AUTH_SERVICE_URI = "http://localhost:8081";

    public static final String PLANNER_SERVICE_ROUTE_NAME = "planner_service";
    public static final String PLANNER_SERVICE_PATH_PATTERN = "/planner-service/tasks/**";
    public static final String PLANNER_SERVICE_REWRITE_PATH = "/planner-service/tasks/(?<remaining>.*)";
    public static final String PLANNER_SERVICE_REWRITE_REPLACEMENT = "/tasks/${remaining}";
    public static final String PLANNER_SERVICE_URI = "http://localhost:8083";
}
