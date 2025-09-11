/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.dto.request.PayoutRequest
 *  dev.gagnon.dto.request.ProcessPayoutRequest
 *  dev.gagnon.dto.response.PayoutResponse
 *  dev.gagnon.service.PayoutService
 */
package dev.gagnon.service;

import dev.gagnon.dto.request.PayoutRequest;
import dev.gagnon.dto.request.ProcessPayoutRequest;
import dev.gagnon.dto.response.PayoutResponse;

import java.util.List;

public interface PayoutService {
    public String requestPayout(PayoutRequest var1);

    public String process(Long var1, ProcessPayoutRequest var2);

    public List<PayoutResponse> getAllPayout();

    public PayoutResponse getPayout(Long var1);

    public List<PayoutResponse> getAllShopPayouts(Long var1);

    public PayoutResponse markAsPaid(Long var1);
}

