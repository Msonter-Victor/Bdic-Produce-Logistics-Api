package dev.gagnon.service;

import dev.gagnon.data.model.User;
import dev.gagnon.dto.request.SignUpRequest;
import dev.gagnon.dto.response.SignUpResponse;

public interface UserService {

    SignUpResponse signUp(SignUpRequest signUpRequest);

    User getByEmail(String email);
}
