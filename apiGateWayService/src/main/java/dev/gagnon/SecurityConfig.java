package dev.gagnon;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Configuration
public class SecurityConfig {
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList(
            "https://marketplace.bdic.ng", 
            "https://digitalmarket.benuestate.gov.ng",
            "http://localhost:3000"
        ));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Cart-Id",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));
        config.setExposedHeaders(Arrays.asList(
                "Authorization",
                "X-Cart-Id",
                "X-Request-ID",
                "Link",
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"
        ));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }

    @Bean
    public WebFilter securityHeadersFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {
            exchange.getResponse().getHeaders().add(
                    "Strict-Transport-Security",
                    "max-age=31536000; includeSubDomains; preload"
            );
            exchange.getResponse().getHeaders().add(
                    "Content-Security-Policy",
                    "default-src 'self'; " +
                            "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdn.jsdelivr.net https://unpkg.com https://cdnjs.cloudflare.com; " +
                            "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com https://cdn.jsdelivr.net; " +
                            "font-src 'self' https://fonts.gstatic.com https://cdn.jsdelivr.net; " +
                            "img-src 'self' data: https: blob:; " +
                            "connect-src 'self' https://api.bdic.ng https://marketplace.bdic.ng wss: ws:; " +
                            "media-src 'self' blob:; " +
                            "object-src 'none'; " +
                            "base-uri 'self'; " +
                            "form-action 'self'; " +
                            "frame-ancestors 'none'; " +
                            "upgrade-insecure-requests"
            );
            exchange.getResponse().getHeaders().add(
                    "X-Content-Type-Options",
                    "nosniff"
            );
            return chain.filter(exchange);
        };
    }
}