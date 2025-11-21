package pl.kwec.mymanagerapigateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "cors")
public final class CorsConfigProperties {

    private final List<String> allowedOrigins = new ArrayList<>();
    private final List<String> allowedMethods = new ArrayList<>();
    private final List<String> allowedHeaders = new ArrayList<>();
    private boolean allowCredentials = false;
    private long maxAge = 3600;

    public CorsConfigProperties() {
        this.allowedOrigins.add("http://localhost:5173");
        this.allowedOrigins.add("http://localhost:3000");
        this.allowedMethods.add("GET");
        this.allowedMethods.add("POST");
        this.allowedMethods.add("PUT");
        this.allowedMethods.add("DELETE");
        this.allowedMethods.add("OPTIONS");
        this.allowedHeaders.add("*");
        this.allowCredentials = true;
    }

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public List<String> getAllowedMethods() {
        return allowedMethods;
    }

    public List<String> getAllowedHeaders() {
        return allowedHeaders;
    }

    public boolean isAllowCredentials() {
        return allowCredentials;
    }

    public void setAllowCredentials(final boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(final long maxAge) {
        this.maxAge = maxAge;
    }
}
