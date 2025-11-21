package pl.kwec.mymanagerapigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Optional;

public final class AuthenticationDetailsExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationDetailsExtractor.class);

    private AuthenticationDetailsExtractor() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Optional<AuthenticationDetails> extract(final Authentication authentication) {
        if (authentication == null) {
            LOGGER.debug("Authentication is null");
            return Optional.empty();
        }

        final Object detailsObj = authentication.getDetails();
        if (!(detailsObj instanceof Map<?, ?> detailsMap)) {
            LOGGER.debug("Authentication details are not a Map");
            return Optional.empty();
        }

        final Map<String, Object> details = (Map<String, Object>) detailsMap;
        final Object userIdObj = details.get("userId");
        final Object emailObj = details.get("email");

        if (userIdObj == null || emailObj == null) {
            LOGGER.warn("Missing required authentication details: userId={}, email={}", userIdObj, emailObj);
            return Optional.empty();
        }

        final String userId = userIdObj.toString();
        final String email = emailObj.toString();

        return Optional.of(new AuthenticationDetails(userId, email));
    }

    public record AuthenticationDetails(String userId, String email) {
    }
}
