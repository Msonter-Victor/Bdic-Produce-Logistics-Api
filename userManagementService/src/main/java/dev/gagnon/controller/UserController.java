package dev.gagnon.controller;

import dev.gagnon.data.model.User;
import dev.gagnon.dto.request.SignUpRequest;
import dev.gagnon.dto.response.SignUpResponse;
import dev.gagnon.exception.BdicBaseException;
import dev.gagnon.security.service.AuthService;
import dev.gagnon.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static dev.gagnon.security.utils.SecurityUtils.JWT_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RestController
@RequestMapping("api/v1/auth")
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest) {
        try{
            SignUpResponse response = userService.signUp(signUpRequest);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        catch (BdicBaseException exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(AUTHORIZATION) String token) {
        token = token.replace(JWT_PREFIX, "").strip();
        authService.blacklist(token);
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        try{
            User user = userService.getByEmail(email);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        catch (BdicBaseException exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/is-token-blacklisted")
    public ResponseEntity<Boolean> isTokenBlacklisted(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        log.info("token check: {}", token);
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        boolean isBlacklisted = authService.isTokenBlacklisted(token);
        return ResponseEntity.ok(isBlacklisted);
    }

}
