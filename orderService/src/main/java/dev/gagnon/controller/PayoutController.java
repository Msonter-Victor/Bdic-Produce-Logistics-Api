/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.controller.PayoutController
 *  dev.gagnon.dto.request.PayoutRequest
 *  dev.gagnon.dto.request.ProcessPayoutRequest
 *  dev.gagnon.dto.response.PayoutResponse
 *  dev.gagnon.exception.OrderBaseException
 *  dev.gagnon.service.PayoutService
 *  lombok.Generated
 *  org.springframework.http.ResponseEntity
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.PutMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RequestParam
 *  org.springframework.web.bind.annotation.RestController
 */
package dev.gagnon.controller;

import dev.gagnon.dto.request.PayoutRequest;
import dev.gagnon.dto.request.ProcessPayoutRequest;
import dev.gagnon.dto.response.PayoutResponse;
import dev.gagnon.exception.OrderBaseException;
import dev.gagnon.service.PayoutService;
import lombok.Generated;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value={"/api/payout"})
public class PayoutController {
    private final PayoutService payoutService;

    @PostMapping(value={"/request"})
    public ResponseEntity<?> requestPayout(@RequestBody PayoutRequest request) {
        try {
            String response = this.payoutService.requestPayout(request);
            return ResponseEntity.ok((Object)response);
        }
        catch (OrderBaseException ex) {
            return ResponseEntity.badRequest().body((Object)ex.getMessage());
        }
    }

    @PutMapping(value={"/process"})
    public ResponseEntity<?> processPayout(@RequestParam Long payoutId, @RequestBody ProcessPayoutRequest request) {
        try {
            String response = this.payoutService.process(payoutId, request);
            return ResponseEntity.ok((Object)response);
        }
        catch (OrderBaseException ex) {
            return ResponseEntity.badRequest().body((Object)ex.getMessage());
        }
    }

    @GetMapping(value={"/all"})
    public ResponseEntity<?> getAllPayout() {
        try {
            List response = this.payoutService.getAllPayout();
            return ResponseEntity.ok((Object)response);
        }
        catch (OrderBaseException ex) {
            return ResponseEntity.badRequest().body((Object)ex.getMessage());
        }
    }

    @GetMapping(value={"/allShop"})
    public ResponseEntity<?> getAllShopPayouts(@RequestParam Long shopId) {
        try {
            List response = this.payoutService.getAllShopPayouts(shopId);
            return ResponseEntity.ok((Object)response);
        }
        catch (OrderBaseException ex) {
            return ResponseEntity.badRequest().body((Object)ex.getMessage());
        }
    }

    @GetMapping(value={"/getById"})
    public ResponseEntity<?> getPayoutById(@RequestParam Long id) {
        try {
            PayoutResponse response = this.payoutService.getPayout(id);
            return ResponseEntity.ok((Object)response);
        }
        catch (OrderBaseException ex) {
            return ResponseEntity.badRequest().body((Object)ex.getMessage());
        }
    }

    @PutMapping(value={"/markAsPaid"})
    public ResponseEntity<?> markPayoutAsPaid(@RequestParam Long id) {
        try {
            PayoutResponse response = this.payoutService.markAsPaid(id);
            return ResponseEntity.ok((Object)response);
        }
        catch (OrderBaseException ex) {
            return ResponseEntity.badRequest().body((Object)ex.getMessage());
        }
    }

    @Generated
    public PayoutController(PayoutService payoutService) {
        this.payoutService = payoutService;
    }
}

