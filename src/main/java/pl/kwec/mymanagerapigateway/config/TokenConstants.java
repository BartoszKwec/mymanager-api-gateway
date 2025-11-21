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
}
