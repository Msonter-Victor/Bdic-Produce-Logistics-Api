package dev.gagnon.DTO.response;

import dev.gagnon.DTO.ShopDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class MarketResponse {
    private Long id;
    private String name;
    private String address;
    private String city;
    private Integer lines;
    private String status;
    private int numberOfShops;
    //    private Integer shops;
    private List<ShopDto> shops;
    private Long councilWardId; // Foreign Key
    private String createdAt;
}
