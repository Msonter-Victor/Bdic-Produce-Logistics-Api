package dev.gagnon.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dev.gagnon.Model.constants.FeaturedStatus;
import dev.gagnon.Model.constants.PromotedStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal price;
    private BigDecimal salesPrice;
    private String description;
    private Integer quantity;
    private BigDecimal discountAmount = BigDecimal.ZERO;
    private String mainImageUrl;
    private String sideImage1Url;
    private String sideImage2Url;
    private String sideImage3Url;
    private String sideImage4Url;
    private Long quantitySold =0L;
    @Setter(AccessLevel.NONE)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;
    @Setter(AccessLevel.NONE)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;
    private FeaturedStatus featuredStatus = FeaturedStatus.NOT_FEATURED;
    private boolean isPromoted = false;
    private boolean isFloated = false;
    private LocalDateTime featuredAt;
    private LocalDateTime promotedAt;
    private LocalDateTime floatedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    @JsonIgnore
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;

    @ManyToOne
    @JoinColumn(name = "sub_category_id")
    @JsonIgnore
    private SubCategory subCategory;

    // Getter for discounted price
    public BigDecimal getDiscountedPrice() {
        return price.subtract(discountAmount != null ? discountAmount : BigDecimal.ZERO);
    }

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
    }
    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
