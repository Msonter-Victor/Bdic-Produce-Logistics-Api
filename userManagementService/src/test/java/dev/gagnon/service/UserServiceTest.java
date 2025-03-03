package dev.gagnon.service;

import dev.gagnon.data.constants.Role;
import dev.gagnon.data.repository.FarmerRepository;
import dev.gagnon.dto.request.SignUpRequest;
import dev.gagnon.dto.response.SignUpResponse;
import dev.gagnon.exception.EmailExistsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private FarmerRepository userRepository;

    @Autowired
    private UserService userService;

    @AfterEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @Sql(scripts = "/db/data.sql")
    public void successfulRegistrationTest(){
        var response = signUp();
        assertThat(response).isNotNull();
    }



    @Test
    @Sql(scripts = "/db/data.sql")
    public void registerUserWithExistingEmailTest(){
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setFirstName("Gagnon");
        signUpRequest.setLastName("Doe");
        signUpRequest.setEmail("hunchogrey73@gmail.com");
        signUpRequest.setPassword("password");
        signUpRequest.setPhone("09022570223");
        signUpRequest.setRole(Role.FARMER);
        assertThrows(EmailExistsException.class,()->userService.signUp(signUpRequest));
    }


    private SignUpResponse signUp() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setFirstName("Gagnon");
        signUpRequest.setLastName("Doe");
        signUpRequest.setEmail("victormsonter@gmail.com");
        signUpRequest.setPassword("password");
        signUpRequest.setPhone("09022570223");
        signUpRequest.setRole(Role.FARMER);
        return userService.signUp(signUpRequest);
    }
}