/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonIgnore
 *  com.fasterxml.jackson.databind.annotation.JsonDeserialize
 *  com.fasterxml.jackson.databind.annotation.JsonSerialize
 *  com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
 *  com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
 *  dev.gagnon.model.DeliveryInfo
 *  dev.gagnon.model.Order
 *  dev.gagnon.model.OrderItem
 *  dev.gagnon.model.Shop
 *  dev.gagnon.model.constants.OrderStatus
 *  jakarta.persistence.CascadeType
 *  jakarta.persistence.Column
 *  jakarta.persistence.Embedded
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
 *  jakarta.persistence.PrePersist
 *  jakarta.persistence.PreUpdate
 *  jakarta.persistence.Table
 *  lombok.Generated
 *  org.hibernate.annotations.Filter
 *  org.hibernate.annotations.FilterDef
 *  org.hibernate.annotations.ParamDef
 *  org.hibernate.annotations.SQLDelete
 *  org.hibernate.annotations.SQLRestriction
 */
package dev.gagnon.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dev.gagnon.model.constants.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.*;
import lombok.Generated;
import org.hibernate.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="_orders")
@SQLRestriction(value="deleted = false")
@SQLDelete(sql="UPDATE _orders SET deleted = true WHERE id = ?")
@FilterDef(name="deletedFilter", parameters={@ParamDef(name="isDeleted", type=Boolean.class)})
@Filter(name="deletedFilter", condition="deleted = :isDeleted")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Order {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String orderNumber;
    private String buyerEmail;
    @Embedded
    private DeliveryInfo deliveryInfo;
    private BigDecimal totalAmount;
    private BigDecimal deliveryFee = BigDecimal.ZERO;
    private BigDecimal grandTotal;
    private String couponApplied;
    @Enumerated(value=EnumType.STRING)
    private OrderStatus status;
    @OneToMany(mappedBy="order", cascade={CascadeType.ALL}, orphanRemoval=true, fetch=FetchType.LAZY)
    @JsonIgnore
    private List<OrderItem> items = new ArrayList();
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="shop_id")
    @JsonIgnore
    private Shop shop;
    @Column(nullable=false, columnDefinition="BOOLEAN DEFAULT FALSE")
    private boolean deleted = false;
    @Column(name="deleted_at")
    private LocalDateTime deletedAt;
    @Column(name="deleted_by")
    private String deletedBy;
    @JsonSerialize(using=LocalDateTimeSerializer.class)
    @JsonDeserialize(using=LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;
    @JsonSerialize(using=LocalDateTimeSerializer.class)
    @JsonDeserialize(using=LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;
    private boolean isParentOrder;
    private String transactionReference;
    @Column(name="parent_order_id")
    private Long parentOrderId;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addOrderItem(OrderItem item) {
        this.items.add(item);
        item.setOrder(this);
    }

    public Long getShopId() {
        return this.shop != null ? this.shop.getId() : null;
    }

}

