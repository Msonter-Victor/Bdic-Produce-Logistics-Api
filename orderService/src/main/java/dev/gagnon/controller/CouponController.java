/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.controller.CouponController
 *  dev.gagnon.dto.request.CouponRequest
 *  dev.gagnon.exception.OrderBaseException
 *  dev.gagnon.service.CouponService
 *  lombok.Generated
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.HttpStatusCode
 *  org.springframework.http.ResponseEntity
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RequestParam
 *  org.springframework.web.bind.annotation.RestController
 */
package dev.gagnon.controller;

import dev.gagnon.dto.request.CouponRequest;
import dev.gagnon.exception.OrderBaseException;
import dev.gagnon.service.CouponService;
import lombok.Generated;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coupon")
public class CouponController {
    private final CouponService couponService;

    @PostMapping(value={"/create-coupon"})
    public ResponseEntity<?> createCoupon(@RequestBody CouponRequest request) {
        try {
            String response = this.couponService.createCoupon(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        catch (OrderBaseException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value={"/get-coupon-discount"})
    public ResponseEntity<?> getCouponDiscount(@RequestParam String couponCode, @RequestParam double amount, @RequestParam String cartId) {
        try {
            double response = this.couponService.getCouponDiscount(couponCode, amount, cartId);
            return ResponseEntity.status((HttpStatusCode)HttpStatus.OK).body((Object)response);
        }
        catch (OrderBaseException ex) {
            return new ResponseEntity((Object)ex.getMessage(), (HttpStatusCode)HttpStatus.BAD_REQUEST);
        }
    }

    @Generated
    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }
}

