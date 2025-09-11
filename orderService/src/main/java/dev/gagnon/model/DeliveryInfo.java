/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.model.DeliveryInfo
 *  jakarta.persistence.Embeddable
 *  lombok.Generated
 */
package dev.gagnon.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DeliveryInfo {
    private String method;
    private String address;
    private String contactPhone;
}

