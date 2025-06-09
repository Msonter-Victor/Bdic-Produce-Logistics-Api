package dev.gagnon.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Builder
public class UpdateRoleRequest {
    private String email;
    private String name;
}
