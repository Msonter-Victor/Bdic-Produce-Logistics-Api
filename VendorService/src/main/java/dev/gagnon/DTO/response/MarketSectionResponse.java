package dev.gagnon.DTO.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MarketSectionResponse {
    private Long id;
    private Long marketId;
    private String name;
    private Long shops;
    private String status;
    private String createdAt;
    private String updatedAt;
}
