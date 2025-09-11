/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.model.CartItem
 *  jakarta.persistence.Entity
 *  jakarta.persistence.GeneratedValue
 *  jakarta.persistence.GenerationType
 *  jakarta.persistence.Id
 *  jakarta.persistence.Table
 *  jakarta.persistence.Version
 *  lombok.Generated
 */
package dev.gagnon.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name="cart_items")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CartItem {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Version
    private Long version;
    private String vendorEmail;
    private Long productId;
    private Long shopId;
    private String name;
    private String description;
    private BigDecimal unitPrice;
    private String productImage;
    private Integer quantity;


}

