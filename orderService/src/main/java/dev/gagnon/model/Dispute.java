/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.annotation.JsonDeserialize
 *  com.fasterxml.jackson.databind.annotation.JsonSerialize
 *  com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
 *  com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
 *  dev.gagnon.model.Dispute
 *  dev.gagnon.model.OrderItem
 *  dev.gagnon.model.constants.DisputeStatus
 *  jakarta.persistence.Entity
 *  jakarta.persistence.GeneratedValue
 *  jakarta.persistence.GenerationType
 *  jakarta.persistence.Id
 *  jakarta.persistence.JoinColumn
 *  jakarta.persistence.ManyToOne
 *  jakarta.persistence.PrePersist
 *  jakarta.persistence.Table
 *  lombok.Generated
 */
package dev.gagnon.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dev.gagnon.model.OrderItem;
import dev.gagnon.model.constants.DisputeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="disputes")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Dispute {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String orderNumber;
    @ManyToOne
    @JoinColumn(name="item_id")
    private OrderItem item;
    private String reason;
    private String imageUrl;
    @JsonSerialize(using=LocalDateTimeSerializer.class)
    @JsonDeserialize(using=LocalDateTimeDeserializer.class)
    private LocalDateTime requestDate;
    private DisputeStatus status = DisputeStatus.PENDING;
    private LocalDateTime resolvedDate;

    @PrePersist
    public void prePersist() {
        this.requestDate = LocalDateTime.now();
    }

}

