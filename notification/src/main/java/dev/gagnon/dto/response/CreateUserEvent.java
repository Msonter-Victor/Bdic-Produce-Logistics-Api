package dev.gagnon.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateUserEvent {
    private String firstName;
    private String email;
}
