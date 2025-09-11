/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonIgnore
 *  dev.gagnon.model.Market
 *  dev.gagnon.model.Order
 *  dev.gagnon.model.Shop
 *  dev.gagnon.model.User
 *  dev.gagnon.model.constants.PromotedStatus
 *  dev.gagnon.model.constants.PromotionType
 *  jakarta.persistence.CascadeType
 *  jakarta.persistence.Column
 *  jakarta.persistence.Entity
 *  jakarta.persistence.EnumType
 *  jakarta.persistence.Enumerated
 *  jakarta.persistence.FetchType
 *  jakarta.persistence.GeneratedValue
 *  jakarta.persistence.GenerationType
 *  jakarta.persistence.Id
 *  jakarta.persistence.JoinColumn
 *  jakarta.persistence.ManyToOne
 *  jakarta.persistence.OneToMany
 *  jakarta.persistence.Table
 *  lombok.Generated
 */
package dev.gagnon.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.gagnon.model.Market;
import dev.gagnon.model.Order;
import dev.gagnon.model.User;
import dev.gagnon.model.constants.PromotedStatus;
import dev.gagnon.model.constants.PromotionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="shops")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Shop {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToMany(mappedBy="shop", cascade={CascadeType.ALL}, fetch=FetchType.LAZY)
    @JsonIgnore
    private List<Order> orders = new ArrayList();
    private BigDecimal totalPayoutAmount;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    @JsonIgnore
    private User user;
    private String bankName;
    private String phone;
    private String accountNumber;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="market_id")
    @JsonIgnore
    private Market market;
    @Enumerated(value=EnumType.STRING)
    @Column(name="promoted_status")
    private PromotedStatus promotedStatus = PromotedStatus.NOT_PROMOTED;
    @Enumerated(value=EnumType.STRING)
    private PromotionType promotionType = PromotionType.NONE;

    public void addOrder(Order order) {
        this.orders.add(order);
        order.setShop(this);
    }

}

