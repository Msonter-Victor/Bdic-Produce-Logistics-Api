/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.dto.request.CouponRequest
 *  dev.gagnon.service.CouponService
 */
package dev.gagnon.service;

import dev.gagnon.dto.request.CouponRequest;

public interface CouponService {
    public String createCoupon(CouponRequest var1);

    public double getCouponDiscount(String var1, double var2, String var4);
}

