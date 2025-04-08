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

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationManager authenticationManager;
    private final RsaKeyProperties rsaKeys;

    public SecurityConfig(AuthenticationManager authenticationManager, RsaKeyProperties rsaKeys) {
        this.authenticationManager = authenticationManager;
        this.rsaKeys = rsaKeys;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        var authenticationFilter = new BdicUsernamePasswordAuthenticationFilter(authenticationManager, rsaKeys);
        authenticationFilter.setFilterProcessesUrl("/api/v1/auth/login");

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/auth/login").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class) // ✅ Add filter here
                .authenticationManager(authenticationManager)
                .build();
    }
}
