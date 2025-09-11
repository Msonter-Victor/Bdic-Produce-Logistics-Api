package dev.gagnon.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmailVerificationEvent {
    private String email;
    private String firstName;
    private String verificationToken;
    private String frontendUrl;

    public EmailVerificationEvent(String email, String firstName, String verificationToken, String frontendUrl) {
        this.email = email;
        this.firstName = firstName;
        this.verificationToken = verificationToken;
        this.frontendUrl = frontendUrl;
    }
}