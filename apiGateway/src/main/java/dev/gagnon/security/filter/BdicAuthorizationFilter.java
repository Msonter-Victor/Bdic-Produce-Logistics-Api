package dev.gagnon.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import dev.gagnon.security.config.RsaKeyProperties;
import dev.gagnon.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static dev.gagnon.security.utils.SecurityUtils.JWT_PREFIX;
import static dev.gagnon.security.utils.SecurityUtils.PUBLIC_ENDPOINTS;

@Component
@AllArgsConstructor
@Slf4j
public class BdicAuthorizationFilter implements WebFilter {

    private final RsaKeyProperties rsaKeys;
    private final AuthService authService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String requestPath = exchange.getRequest().getURI().getPath();

        // If the endpoint is public, continue without checking authentication.
        if (PUBLIC_ENDPOINTS.contains(requestPath)) {
            log.info("Authorization not needed for public endpoint: {}", requestPath);
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(JWT_PREFIX)) {
            String token = authHeader.substring(JWT_PREFIX.length()).trim();

            // Wrap the blocking token blacklist check
            return Mono.fromCallable(() -> authService.isTokenBlacklisted(token))
                    .subscribeOn(Schedulers.boundedElastic())
                    .flatMap(isBlacklisted -> {
                        if (isBlacklisted) {
                            log.warn("Token is blacklisted: {}", token);
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        }

                        // Validate the token (this operation may also be blocking)
                        try {
                            Algorithm algorithm = Algorithm.RSA512(rsaKeys.publicKey(), rsaKeys.privateKey());
                            JWTVerifier jwtVerifier = JWT.require(algorithm)
                                    .withIssuer("BenueProduceLogApp")
                                    .withClaimPresence("roles")
                                    .withClaimPresence("principal")
                                    .withClaimPresence("credentials")
                                    .build();

                            DecodedJWT decodedJWT = jwtVerifier.verify(token);
                            List<? extends GrantedAuthority> authorities = decodedJWT.getClaim("roles")
                                    .asList(SimpleGrantedAuthority.class);
                            String principal = decodedJWT.getClaim("principal").asString();
                            String credentials = decodedJWT.getClaim("credentials").asString();

                            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, credentials, authorities);
                            log.info("User authorization succeeded for principal: {}", principal);

                            // Propagate the authentication in the reactive context
                            return chain.filter(exchange)
                                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                        } catch (JWTVerificationException exception) {
                            log.error("JWT verification failed: {}", exception.getMessage());
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        }
                    });
        }

        // If no valid Authorization header is present, return unauthorized.
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
