/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.annotation.JsonDeserialize
 *  com.fasterxml.jackson.databind.annotation.JsonSerialize
 *  com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
 *  com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
 *  dev.gagnon.model.Refund
 *  dev.gagnon.model.constants.RefundStatus
 *  jakarta.persistence.Column
 *  jakarta.persistence.Entity
 *  jakarta.persistence.EnumType
 *  jakarta.persistence.Enumerated
 *  jakarta.persistence.GeneratedValue
 *  jakarta.persistence.GenerationType
 *  jakarta.persistence.Id
 *  jakarta.persistence.PrePersist
 *  jakarta.persistence.PreUpdate
 *  jakarta.persistence.Table
 *  lombok.Generated
 */
package dev.gagnon.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dev.gagnon.model.constants.RefundStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="refunds")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Refund {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private String orderNumber;
    private String buyerName;
    private String vendorName;
    private Long shopId;
    private BigDecimal refundAmount;
    private String reason;
    @Enumerated(value=EnumType.STRING)
    @Column(name="status")
    private RefundStatus status;
    private String adminNotes;
    @JsonSerialize(using=LocalDateTimeSerializer.class)
    @JsonDeserialize(using=LocalDateTimeDeserializer.class)
    @Column(name="created_at")
    private LocalDateTime createdAt;
    @JsonSerialize(using=LocalDateTimeSerializer.class)
    @JsonDeserialize(using=LocalDateTimeDeserializer.class)
    @Column(name="updated_at")
    private LocalDateTime updatedAt;
    @JsonSerialize(using=LocalDateTimeSerializer.class)
    @JsonDeserialize(using=LocalDateTimeDeserializer.class)
    @Column(name="processed_at")
    private LocalDateTime processedAt;

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


}

