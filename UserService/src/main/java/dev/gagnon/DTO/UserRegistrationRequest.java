package dev.gagnon.DTO;

import lombok.Data;
@Data
public class UserRegistrationRequest {
    private String email;
    private String surname;
    private String otherName;
    private String password;
    private String phone;       // optional
    private String nin;         // optional
    private String passportUrl; // optional
}