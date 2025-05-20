package dev.gagnon.DTO;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StatusResponseDTO {
    private Long id;
    private String name;
    private Long createdById;
    private String createdByUsername;
    private LocalDateTime createdAt;
}
