package dev.gagnon.security.filter;


//@Component
//@Slf4j
//@AllArgsConstructor
public class BdicAuthenticationFilter{

//    private final RsaKeyProperties rsaKeys;
//
//    public BdicAuthenticationFilter(RsaKeyProperties rsaKeys) {
//        this.rsaKeys = rsaKeys;
//    }
//    private final AuthService authService;
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        log.info("authentication started");
//        String requestPath = exchange.getRequest().getURI().getPath();
//        log.info("request path: {}", requestPath);
//
//        // If the endpoint is public, continue without checking authentication.
//        if (PUBLIC_ENDPOINTS.contains(requestPath)) {
//            log.info("Authorization not needed for public endpoint: {}", requestPath);
//            return chain.filter(exchange);
//        }
//        log.info("authorization needed");
//
//        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//        if (authHeader != null && authHeader.startsWith(JWT_PREFIX)) {
//            String token = authHeader.substring(JWT_PREFIX.length()).trim();
//
//            // Wrap the blocking token blacklist check
//            return Mono.fromCallable(() -> authService.isTokenBlacklisted(token))
//                    .subscribeOn(Schedulers.boundedElastic())
//                    .flatMap(isBlacklisted -> {
//                        if (isBlacklisted) {
//                            log.warn("Token is blacklisted: {}", token);
//                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                            return exchange.getResponse().setComplete();
//                        }
//
//                        // Validate the token (this operation may also be blocking)
//                        try {
//                            Algorithm algorithm = Algorithm.RSA512(rsaKeys.publicKey(), rsaKeys.privateKey());
//                            JWTVerifier jwtVerifier = JWT.require(algorithm)
//                                    .withIssuer("BenueProduceLogApp")
//                                    .withClaimPresence("roles")
//                                    .withClaimPresence("principal")
//                                    .withClaimPresence("credentials")
//                                    .build();
//
//                            DecodedJWT decodedJWT = jwtVerifier.verify(token);
//                            List<? extends GrantedAuthority> authorities = decodedJWT.getClaim("roles")
//                                    .asList(SimpleGrantedAuthority.class);
//                            String principal = decodedJWT.getClaim("principal").asString();
//                            String credentials = decodedJWT.getClaim("credentials").asString();
//
//                            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, credentials, authorities);
//                            log.info("User authorization succeeded for principal: {}", principal);
//
//                            // Propagate the authentication in the reactive context
//                            return chain.filter(exchange)
//                                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
//                        } catch (JWTVerificationException exception) {
//                            log.error("JWT verification failed: {}", exception.getMessage());
//                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                            return exchange.getResponse().setComplete();
//                        }
//                    });
//        }
//
//        // If no valid Authorization header is present, return unauthorized.
//        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//        return exchange.getResponse().setComplete();
//    }
}
