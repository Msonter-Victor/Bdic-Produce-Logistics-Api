package dev.gagnon.controller;

import dev.gagnon.data.model.User;
import dev.gagnon.dto.request.SignUpRequest;
import dev.gagnon.dto.response.SignUpResponse;
import dev.gagnon.exception.BdicBaseException;
import dev.gagnon.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
}
