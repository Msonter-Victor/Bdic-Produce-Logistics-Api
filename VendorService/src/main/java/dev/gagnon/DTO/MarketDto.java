package dev.gagnon.DTO;


import dev.gagnon.Model.Shop;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
public class MarketDto {
    private String name;
    private String address;
    private String city;
    private Integer lines;
//    private Integer shops;
    private List<Shop> shops;
    private Long councilWardId; // Foreign Key
}
