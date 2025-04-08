package dev.gagnon.security.config;

import dev.gagnon.security.filter.BdicAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
//@EnableWebFluxSecurity
public class GatewaySecurityConfig {
//
//    private final BdicAuthenticationFilter jwtAuthenticationFilter;
//
//    public GatewaySecurityConfig(BdicAuthenticationFilter jwtAuthenticationFilter) {
//        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//    }
//
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        return http
//                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .authorizeExchange(exchanges -> exchanges
//                        .pathMatchers(HttpMethod.POST).permitAll()
//                        .pathMatchers("/api/v1/auth/login").permitAll()
//                        .pathMatchers("/api/v1/auth/register").permitAll()
//                        .pathMatchers("/api/v1/mail/send").permitAll()
//                        .pathMatchers("/api/v1/farmer/**").hasAuthority("FARMER")
//                        .anyExchange().authenticated()
//                )
//                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
//                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
//                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
//                .build();
//    }
//
//
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.setAllowedOrigins(List.of("http://localhost:3000", "https://benue-produce-and-logistics.vercel.app"));
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        config.setAllowedHeaders(List.of("*"));
//        config.setExposedHeaders(List.of("Authorization", "Link"));
//        config.setMaxAge(3600L);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return source;
//    }
}
