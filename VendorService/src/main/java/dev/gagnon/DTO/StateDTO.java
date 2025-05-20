package dev.gagnon.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StateDTO {
    private Long id;
    private String name;
}
