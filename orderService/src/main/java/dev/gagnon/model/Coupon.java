/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.JsonSerializer
 *  com.fasterxml.jackson.databind.annotation.JsonSerialize
 *  dev.gagnon.model.Coupon
 *  dev.gagnon.model.Shop
 *  jakarta.persistence.Entity
 *  jakarta.persistence.GeneratedValue
 *  jakarta.persistence.GenerationType
 *  jakarta.persistence.Id
 *  jakarta.persistence.JoinColumn
 *  jakarta.persistence.ManyToOne
 *  jakarta.persistence.Table
 *  lombok.Generated
 */
package dev.gagnon.model;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.gagnon.model.Shop;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="_coupons")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Coupon {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    private String code;
    @ManyToOne
    @JoinColumn(name="shop_id")
    private Shop shop;
    private int percentageDiscount;
    @JsonSerialize(using=JsonSerializer.class)
    private LocalDateTime createdAt;

}

