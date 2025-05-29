package dev.gagnon.DTO;

import dev.gagnon.Model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    public UserResponse(User user) {
        this.id = user.getId();
        this.firstName = user.getSurname();
        this.lastName =user.getOtherName();
        this.email = user.getEmail();
    }
}
