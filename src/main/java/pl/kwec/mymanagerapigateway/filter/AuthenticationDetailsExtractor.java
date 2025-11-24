package pl.kwec.mymanagerapigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import pl.kwec.mymanagerapigateway.config.TokenConstants;

import java.util.Map;
import java.util.Optional;

public final class AuthenticationDetailsExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationDetailsExtractor.class);
    private static final String AUTH_IS_NULL = "Authentication is null";
    private static final String DETAILS_NOT_MAP = "Authentication details are not a Map";
    private static final String MISSING_DETAILS = "Missing required authentication details: userId={}, email={}";

    private AuthenticationDetailsExtractor() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Optional<AuthenticationDetails> extract(final Authentication authentication) {
        if (authentication == null) {
            LOGGER.debug(AUTH_IS_NULL);
            return Optional.empty();
        }

        final Object detailsObj = authentication.getDetails();
        if (!(detailsObj instanceof Map<?, ?> detailsMap)) {
            LOGGER.debug(DETAILS_NOT_MAP);
            return Optional.empty();
        }

        final Map<String, Object> details = (Map<String, Object>) detailsMap;
        final Object userIdObj = details.get(TokenConstants.CLAIM_USER_ID);
        final Object emailObj = details.get(TokenConstants.CLAIM_EMAIL);

        if (userIdObj == null || emailObj == null) {
            LOGGER.warn(MISSING_DETAILS, userIdObj, emailObj);
            return Optional.empty();
        }

        final String userId = userIdObj.toString();
        final String email = emailObj.toString();

        return Optional.of(new AuthenticationDetails(userId, email));
    }

    public record AuthenticationDetails(String userId, String email) {
    }
}
