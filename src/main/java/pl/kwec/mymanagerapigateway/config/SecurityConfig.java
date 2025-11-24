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
    private static final String SECURITY_CONFIG_INITIALIZED = "Security configuration initialized";
    private static final String MISSING_JWT_CLAIMS = "Missing required JWT claims: userId={}, email={}, role={}";
    private static final String JWT_VALIDATED = "JWT validated for user: {}";
    private static final String ERROR_PROCESSING_JWT = "Error processing JWT token";

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(final ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(TokenConstants.PUBLIC_PATH_PATTERN).permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor()))
                )
                .cors(cors -> cors.configurationSource(request -> {
                    final CorsConfiguration config = new CorsConfiguration();
                    config.setAllowCredentials(true);
                    config.addAllowedOrigin(TokenConstants.CORS_ALLOWED_ORIGIN);
                    config.addAllowedHeader(TokenConstants.CORS_ALLOWED_HEADER);
                    config.addAllowedMethod(TokenConstants.CORS_ALLOWED_METHOD);
                    return config;
                }));

        LOGGER.info(SECURITY_CONFIG_INITIALIZED);
        return http.build();
    }

    private Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        return jwt -> {
            try {
                final String userId = jwt.getClaimAsString(TokenConstants.CLAIM_USER_ID);
                final String email = jwt.getSubject();
                final String role = jwt.getClaimAsString(TokenConstants.CLAIM_ROLE);

                if (userId == null || email == null || role == null) {
                    LOGGER.warn(MISSING_JWT_CLAIMS, userId, email, role);
                    return Mono.empty();
                }

                final List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(TokenConstants.ROLE_PREFIX + role));

                final Map<String, Object> details = Map.of(
                        TokenConstants.CLAIM_USER_ID, userId,
                        TokenConstants.CLAIM_EMAIL, email
                );

                LOGGER.debug(JWT_VALIDATED, userId);
                return Mono.just(new UsernamePasswordAuthenticationToken(email, null, authorities) {{
                    setDetails(details);
                }});
            } catch (final Exception e) {
                LOGGER.error(ERROR_PROCESSING_JWT, e);
                return Mono.error(e);
            }
        };
    }
}
