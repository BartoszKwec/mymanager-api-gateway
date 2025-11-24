package pl.kwec.mymanagerapigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRouteLocator(final RouteLocatorBuilder builder) {
        return builder.routes()
                .route(TokenConstants.AUTH_SERVICE_ROUTE_NAME, r -> r.path(TokenConstants.AUTH_SERVICE_PATH_PATTERN)
                        .filters(f -> f.rewritePath(TokenConstants.AUTH_SERVICE_REWRITE_PATH, TokenConstants.AUTH_SERVICE_REWRITE_REPLACEMENT))
                        .uri(TokenConstants.AUTH_SERVICE_URI))
                .route(TokenConstants.PLANNER_SERVICE_ROUTE_NAME, r -> r.path(TokenConstants.PLANNER_SERVICE_PATH_PATTERN)
                        .filters(f -> f.rewritePath(TokenConstants.PLANNER_SERVICE_REWRITE_PATH, TokenConstants.PLANNER_SERVICE_REWRITE_REPLACEMENT))
                        .uri(TokenConstants.PLANNER_SERVICE_URI))
                .build();
    }
}
