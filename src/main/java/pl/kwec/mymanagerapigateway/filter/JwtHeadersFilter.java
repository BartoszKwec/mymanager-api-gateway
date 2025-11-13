package pl.kwec.mymanagerapigateway.filter;

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

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        return exchange.getPrincipal()
                .cast(Authentication.class)
                .flatMap(auth -> {
                    Object detailsObj = auth.getDetails();
                    if (detailsObj instanceof Map<?, ?> detailsMap) {
                        Map<String, Object> details = (Map<String, Object>) detailsMap;
                        ServerWebExchange mutatedExchange = exchange.mutate()
                                .request(exchange.getRequest().mutate()
                                        .header("X-User-Id", details.get("userId").toString())
                                        .header("X-User-Email", details.get("email").toString())
                                        .build())
                                .build();
                        return chain.filter(mutatedExchange);
                    }
                    return chain.filter(exchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}