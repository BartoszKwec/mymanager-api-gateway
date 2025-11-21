package pl.kwec.mymanagerapigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class JwtHeadersFilter implements GlobalFilter, Ordered {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtHeadersFilter.class);
    private static final int FILTER_ORDER = 0;
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_EMAIL = "X-User-Email";

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        return exchange.getPrincipal()
                .cast(Authentication.class)
                .map(auth -> enrichExchange(exchange, auth))
                .onErrorResume(e -> {
                    LOGGER.debug("Error processing authentication", e);
                    return Mono.just(exchange);
                })
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }

    private ServerWebExchange enrichExchange(final ServerWebExchange exchange, final Authentication auth) {
        final Object detailsObj = auth.getDetails();
        if (!(detailsObj instanceof Map<?, ?> detailsMap)) {
            LOGGER.debug("Authentication details are not a Map, proceeding without headers");
            return exchange;
        }

        final Map<String, Object> details = (Map<String, Object>) detailsMap;
        final Object userIdObj = details.get("userId");
        final Object emailObj = details.get("email");

        if (userIdObj == null || emailObj == null) {
            LOGGER.debug("Missing userId or email in authentication details");
            return exchange;
        }

        final String userId = userIdObj.toString();
        final String email = emailObj.toString();

        LOGGER.debug("Adding authentication headers for user: {}", userId);
        return exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header(HEADER_USER_ID, userId)
                        .header(HEADER_USER_EMAIL, email)
                        .build())
                .build();
    }

    @Override
    public int getOrder() {
        return FILTER_ORDER;
    }
}