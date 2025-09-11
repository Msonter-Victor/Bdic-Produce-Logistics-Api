/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.dto.response.ShopStatistic
 *  dev.gagnon.dto.response.ShopStatistic$ShopStatisticBuilder
 *  lombok.Generated
 */
package dev.gagnon.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ShopStatistic {
    private Long pendingOrderCount;
    private Long deliveredOrderCount;
    private Long shippedOrderCount;
    private Long totalOrderCount;
    private Long processingOrderCount;

}

