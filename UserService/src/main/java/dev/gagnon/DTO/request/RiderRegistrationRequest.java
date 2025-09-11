package dev.gagnon.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RiderRegistrationRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
}
