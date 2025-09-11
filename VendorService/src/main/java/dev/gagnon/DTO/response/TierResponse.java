package dev.gagnon.DTO.response;

import dev.gagnon.Model.ShopTier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TierResponse {
    private Long id;
    private String tier;
    private double price;
    private Long shopsPromoted;
    private Long featuredNumber;
    private Long promotedNumber;
    private Long floatedNumber;
    private LocalDateTime updateTime;
    public TierResponse(ShopTier tier) {
        this.id = tier.getId();
        this.tier = tier.getTierName();
        this.featuredNumber = tier.getFeaturedNumber();
        this.promotedNumber = tier.getPromotedNumber();
        this.floatedNumber = tier.getFloatedNumber();
        this.price = tier.getPrice();
        this.updateTime = tier.getLastUpdate();
        this.shopsPromoted = tier.getShopsPromoted();
    }
}
