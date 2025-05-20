package dev.gagnon.DTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MarketSectionDto {
    private Long id;
    private Long marketId;
    private String name;
    private String description;
    private String createdAt;
    private String updatedAt;
}
