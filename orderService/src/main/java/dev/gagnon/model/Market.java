/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.annotation.JsonDeserialize
 *  com.fasterxml.jackson.databind.annotation.JsonSerialize
 *  com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
 *  com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
 *  dev.gagnon.model.Market
 *  dev.gagnon.model.Market$MarketBuilder
 *  jakarta.persistence.Column
 *  jakarta.persistence.Entity
 *  jakarta.persistence.GeneratedValue
 *  jakarta.persistence.GenerationType
 *  jakarta.persistence.Id
 *  jakarta.persistence.PrePersist
 *  jakarta.persistence.Table
 *  lombok.Generated
 */
package dev.gagnon.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="markets")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Market {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String city;
    private Integer lines;
    @Column(name="created_at", updatable=false)
    @JsonSerialize(using=LocalDateTimeSerializer.class)
    @JsonDeserialize(using=LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}

