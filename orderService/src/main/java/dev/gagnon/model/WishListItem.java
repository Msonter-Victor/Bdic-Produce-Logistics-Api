/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonIgnore
 *  dev.gagnon.model.WishList
 *  dev.gagnon.model.WishListItem
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.gagnon.model.WishList;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name="wishlist_items")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class WishListItem {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name="wishlist_id")
    @JsonIgnore
    private WishList wishlist;
    private Long productId;
    private String description;
    private String name;
    private BigDecimal unitPrice;
    private String productImage;
    private Integer quantity;

}

