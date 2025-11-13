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
                .route("auth_service", r -> r.path("/auth-service/auth/**")
                        .filters(f -> f.rewritePath("/auth-service/auth/(?<remaining>.*)", "/auth/${remaining}"))
                        .uri("lb://AUTH-SERVICE"))
                .route("planner_service", r -> r.path("/planner-service/tasks/**")
                        .filters(f -> f.rewritePath("/planner-service/tasks/(?<remaining>.*)", "/tasks/${remaining}"))
                        .uri("lb://PLANNER-SERVICE"))
                .build();
    }
}
