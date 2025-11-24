package pl.kwec.mymanagerapigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
public class JwtConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtConfig.class);
    private static final String ALGORITHM = "HmacSHA256";
    private static final int MIN_SECRET_LENGTH = 32;
    private static final String JWT_DECODER_INIT = "Initializing JWT decoder with secret of length: {}";
    private static final String JWT_DECODER_INITIALIZED = "JWT decoder initialized successfully";
    private static final String JWT_SECRET_NOT_CONFIGURED = "JWT secret is not configured";
    private static final String JWT_SECRET_MUST_BE_CONFIGURED = "JWT secret must be configured via JWT_SECRET environment variable";
    private static final String JWT_SECRET_LENGTH_WARNING = "JWT secret length is {} bytes, recommended minimum is {} bytes";

    @Value("${spring.security.oauth2.resourceserver.jwt.secret:supersecretkey1234567890supersecretkey}")
    private String secret;

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        validateSecret();
        LOGGER.debug(JWT_DECODER_INIT, secret.length());

        final SecretKey key = new SecretKeySpec(secret.getBytes(), ALGORITHM);
        final NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        LOGGER.info(JWT_DECODER_INITIALIZED);
        return decoder;
    }

    private void validateSecret() {
        if (secret == null || secret.isEmpty()) {
            LOGGER.error(JWT_SECRET_NOT_CONFIGURED);
            throw new IllegalStateException(JWT_SECRET_MUST_BE_CONFIGURED);
        }
        if (secret.length() < MIN_SECRET_LENGTH) {
            LOGGER.warn(JWT_SECRET_LENGTH_WARNING, secret.length(), MIN_SECRET_LENGTH);
        }
    }
}
