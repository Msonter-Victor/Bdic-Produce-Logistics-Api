package dev.gagnon.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        //.requestMatchers("/api/roles").permitAll()  // only POST /api/roles is public for now
                        .requestMatchers("/api/statuses/**").permitAll()
                        .requestMatchers("/api/statuses/viewAllStatus").permitAll()
                        .requestMatchers("/api/statuses/addStatus").permitAll()
                        .requestMatchers("/api/statuses/get/{id}").permitAll()
                        .requestMatchers("/api/statuses/delete/{id}").permitAll()
                        .requestMatchers("/api/statuses/update/{id}").permitAll()

                        .requestMatchers("/api/markets/**").permitAll()
                        .requestMatchers("/api/review/**").permitAll()
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

                        .requestMatchers("/api/categories/**").permitAll()
                        .requestMatchers("/api/categories/add").permitAll()
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
                .build();
    }

}
