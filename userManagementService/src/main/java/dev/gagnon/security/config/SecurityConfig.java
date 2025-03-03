package dev.gagnon.security.config;

import dev.gagnon.security.filter.BdicUsernamePasswordAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

import static dev.gagnon.security.utils.SecurityUtils.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationManager authenticationManager;
    private final RsaKeyProperties rsaKeys;
    private final BdicAuthorizationFilter authorizationFilter;

    public SecurityConfig(AuthenticationManager authenticationManager, BdicAuthorizationFilter bdicAuthorizationFilter, RsaKeyProperties rsaKeys) {
        this.authenticationManager = authenticationManager;
        this.authorizationFilter = bdicAuthorizationFilter;
        this.rsaKeys = rsaKeys;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        var authenticationFilter =
                new BdicUsernamePasswordAuthenticationFilter(authenticationManager, rsaKeys);
        authenticationFilter.setFilterProcessesUrl("/api/v1/auth/login");

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .addFilterAt(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authorizationFilter, BdicUsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS.toArray(new String[0])).permitAll()
                        .requestMatchers(FARMER_ENDPOINTS).hasAnyAuthority("FARMER")
                        .requestMatchers(BUYER_ENDPOINTS).hasAnyAuthority("BUYER")
                        .requestMatchers(RIDER_ENDPOINTS).hasAnyAuthority("LOGISTICS")
                        .requestMatchers("/api/v1/user/uploadProfilePhoto").hasAnyAuthority("FARMER", "BUYER", "LOGISTICS", "ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .build();
    }



    @Bean
    public CorsFilter corsFilter() {
        CorsConfigurationSource source = corsConfigurationSource();
        return new CorsFilter(source);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "https://benue-produce-and-logistics.vercel.app"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
