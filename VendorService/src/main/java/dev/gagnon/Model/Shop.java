package dev.gagnon.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dev.gagnon.Model.constants.PromotedStatus;
import dev.gagnon.Model.constants.ShopStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "shops")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String logoImage;
    private String shopNumber;
    private String homeAddress;
    private String streetName;
    private String cacNumber;
    private String taxIdNumber;
    private String nin;
    private String bankName;
    private String phone;
    private String accountNumber;
    private String bankCode;
    private String logoUrl;
    private BigDecimal totalPayoutAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_id")
    @JsonIgnore
    private Market market;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_section_id")
    @JsonIgnore
    private MarketSection marketSection;

    @ManyToOne
    @JoinColumn(name = "shop_tier_id")
    private ShopTier shopTier;
    private Long featuredNumber =0L;
    private Long promotedNumber=0L;
    private Long floatedNumber=0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
    private ShopStatus status;
    @Setter(AccessLevel.NONE)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "promoted_status")
    private PromotedStatus promotedStatus = PromotedStatus.NOT_PROMOTED;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime lastPromotedAt;
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
