package dev.gagnon.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouncilWardDTO {
    private Long id;
    private String name;
    private String code;
    private Long localGovernmentId;
}
