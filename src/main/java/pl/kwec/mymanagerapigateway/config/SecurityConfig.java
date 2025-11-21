package pl.kwec.mymanagerapigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);
    private static final String PUBLIC_PATH_PATTERN = "/auth-service/auth/**";
    private static final String CORS_ALLOWED_ORIGIN = "http://localhost:5173";

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(final ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(PUBLIC_PATH_PATTERN).permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor()))
                )
                .cors(cors -> cors.configurationSource(request -> {
                    final CorsConfiguration config = new CorsConfiguration();
                    config.setAllowCredentials(true);
                    config.addAllowedOrigin(CORS_ALLOWED_ORIGIN);
                    config.addAllowedHeader("*");
                    config.addAllowedMethod("*");
                    return config;
                }));

        LOGGER.info("Security configuration initialized");
        return http.build();
    }

    private Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        return jwt -> {
            try {
                final String userId = jwt.getClaimAsString("userId");
                final String email = jwt.getSubject();
                final String role = jwt.getClaimAsString("role");

                if (userId == null || email == null || role == null) {
                    LOGGER.warn("Missing required JWT claims: userId={}, email={}, role={}", userId, email, role);
                    return Mono.empty();
                }

                final List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

                final Map<String, Object> details = Map.of(
                        "userId", userId,
                        "email", email
                );

                LOGGER.debug("JWT validated for user: {}", userId);
                return Mono.just(new UsernamePasswordAuthenticationToken(email, null, authorities) {{
                    setDetails(details);
                }});
            } catch (final Exception e) {
                LOGGER.error("Error processing JWT token", e);
                return Mono.error(e);
            }
        };
    }
}
