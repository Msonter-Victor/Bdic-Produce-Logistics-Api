package dev.gagnon.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalGovernmentDTO {
    private Long id;
    private Long stateId;
  //  private Integer id;
    private String name;
    //private Integer stateId; // For linking to State

}
