package dev.gagnon.Config;

import dev.gagnon.Filter.JwtAuthenticationFilter;
import dev.gagnon.Service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        //.requestMatchers("/api/roles").permitAll()  // only POST /api/roles is public for now
                        .requestMatchers("/api/statuses/**").permitAll()
                        .requestMatchers("/api/statuses/viewAllStatus").permitAll()
                        .requestMatchers("/api/statuses/addStatus").permitAll()
                        .requestMatchers("/api/statuses/get/{id}").permitAll()
                        .requestMatchers("/api/statuses/delete/{id}").permitAll()
                        .requestMatchers("/api/statuses/update/{id}").permitAll()

                        .requestMatchers("/api/markets/**").permitAll()
                        .requestMatchers("/api/markets/all").permitAll()
                        .requestMatchers("/api/markets/add").permitAll()
                        .requestMatchers("/api/markets/{id}").permitAll()
                        .requestMatchers("/api/markets/delete/{id}").permitAll()
                        .requestMatchers("/api/markets/update/{id}").permitAll()

                        .requestMatchers("/api/market-sections/**").permitAll()
                        .requestMatchers("/api/market-sections/add").permitAll()
                        .requestMatchers("/api/market-sections/all").permitAll()
                        .requestMatchers("/api/market-sections/update/{id}").permitAll()

                        .requestMatchers("/api/shops/add").permitAll()
                        .requestMatchers("/api/shops/all").permitAll()
                        .requestMatchers("/api/shops/**").permitAll()
                        .requestMatchers("/api/shops/update/{id}").permitAll()

                        .requestMatchers("/api/states/**").permitAll()
                        .requestMatchers("/api/local-governments/**").permitAll()
                        .requestMatchers("/api/council-wards/**").permitAll()

                        //.requestMatchers("/api/categories/**").authenticated()
                        .requestMatchers("/api/categories/add").authenticated()
                        .requestMatchers("/api/categories/all").permitAll()

                        .requestMatchers("/api/products/**").permitAll()
                        .requestMatchers("/api/products/all").permitAll()
                        .requestMatchers("/api/products/add").authenticated()
                        .requestMatchers("/api/products/{id}").permitAll()
                        .requestMatchers("/api/products/update/{id}").authenticated()

                        .requestMatchers("/api/buyer/**").hasAuthority("BUYER")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(daoAuthenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
