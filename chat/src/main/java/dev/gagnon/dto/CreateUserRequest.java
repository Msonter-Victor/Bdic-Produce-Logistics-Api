package dev.gagnon.dto;

import dev.gagnon.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    private String username;
    private String email;
    private String fullName;
    private UserRole role;
}