package dev.gagnon.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddMarketRequest {
    private Long stateId;
    private Long localGovernmentId;
    private Long councilId;
    private String location;
    private String marketName;
    private Long lines;
    private Long shopNumber;
}