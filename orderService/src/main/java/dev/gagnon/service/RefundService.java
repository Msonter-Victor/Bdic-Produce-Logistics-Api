/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.model.Refund
 *  dev.gagnon.model.constants.RefundStatus
 *  dev.gagnon.service.RefundService
 *  org.springframework.data.domain.Page
 *  org.springframework.data.domain.Pageable
 */
package dev.gagnon.service;

import dev.gagnon.dto.request.RequestRefundRequest;
import dev.gagnon.dto.response.RefundResponse;
import dev.gagnon.model.Refund;
import dev.gagnon.model.constants.RefundStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface RefundService {

    String requestRefund(RequestRefundRequest request);

    String process(Long id);

    List<RefundResponse> getAll();

    List<RefundResponse> getAllPendingRefunds();

    List<RefundResponse> getAllProcessedRefunds();

    RefundResponse getById(Long id);

    Double getTotalProcessedAmount();
}

