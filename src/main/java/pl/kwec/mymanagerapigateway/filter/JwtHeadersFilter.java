package pl.kwec.mymanagerapigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import pl.kwec.mymanagerapigateway.config.TokenConstants;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class JwtHeadersFilter implements GlobalFilter, Ordered {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtHeadersFilter.class);
    private static final int FILTER_ORDER = 0;
    private static final String ERROR_PROCESSING_AUTH = "Error processing authentication";
    private static final String DETAILS_NOT_MAP = "Authentication details are not a Map, proceeding without headers";
    private static final String MISSING_DETAILS = "Missing userId or email in authentication details";
    private static final String ADDING_AUTH_HEADERS = "Adding authentication headers for user: {}";

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        return exchange.getPrincipal()
                .cast(Authentication.class)
                .map(auth -> enrichExchange(exchange, auth))
                .onErrorResume(e -> {
                    LOGGER.debug(ERROR_PROCESSING_AUTH, e);
                    return Mono.just(exchange);
                })
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }

    private ServerWebExchange enrichExchange(final ServerWebExchange exchange, final Authentication auth) {
        final Object detailsObj = auth.getDetails();
        if (!(detailsObj instanceof Map<?, ?> detailsMap)) {
            LOGGER.debug(DETAILS_NOT_MAP);
            return exchange;
        }

        final Map<String, Object> details = (Map<String, Object>) detailsMap;
        final Object userIdObj = details.get(TokenConstants.CLAIM_USER_ID);
        final Object emailObj = details.get(TokenConstants.CLAIM_EMAIL);

        if (userIdObj == null || emailObj == null) {
            LOGGER.debug(MISSING_DETAILS);
            return exchange;
        }

        final String userId = userIdObj.toString();
        final String email = emailObj.toString();
        final String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        LOGGER.debug(ADDING_AUTH_HEADERS, userId);
        return exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header(TokenConstants.HEADER_USER_ID, userId)
                        .header(TokenConstants.HEADER_USER_EMAIL, email)
                        .header("Authorization", authHeader)
                        .build())
                .build();
    }

    @Override
    public int getOrder() {
        return FILTER_ORDER;
    }
}