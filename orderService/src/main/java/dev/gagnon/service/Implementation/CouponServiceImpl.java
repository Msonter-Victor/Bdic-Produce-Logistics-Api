/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.dto.request.CouponRequest
 *  dev.gagnon.exception.BusinessException
 *  dev.gagnon.exception.ResourceNotFoundException
 *  dev.gagnon.model.Coupon
 *  dev.gagnon.model.Shop
 *  dev.gagnon.model.constants.PromotedStatus
 *  dev.gagnon.repository.CouponRepository
 *  dev.gagnon.repository.ShopRepository
 *  dev.gagnon.service.CouponService
 *  dev.gagnon.service.Implementation.CouponServiceImpl
 *  lombok.Generated
 *  org.springframework.stereotype.Service
 */
package dev.gagnon.service.Implementation;

import dev.gagnon.dto.request.CouponRequest;
import dev.gagnon.exception.BusinessException;
import dev.gagnon.exception.ResourceNotFoundException;
import dev.gagnon.model.Coupon;
import dev.gagnon.model.Shop;
import dev.gagnon.model.constants.PromotedStatus;
import dev.gagnon.repository.CouponRepository;
import dev.gagnon.repository.ShopRepository;
import dev.gagnon.service.CouponService;
import lombok.Generated;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

@Service
public class CouponServiceImpl
implements CouponService {
    private final ShopRepository shopRepository;
    private final CouponRepository coupons;

    public String createCoupon(CouponRequest request) {
        String couponCode;
        Shop shop = shopRepository.findById(request.getShopId()).orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        if (shop.getPromotedStatus() == PromotedStatus.NOT_PROMOTED) {
            throw new BusinessException("Shop cannot create coupon");
        }
        if (request.getPercentageDiscount() < 1 || request.getPercentageDiscount() > 100) {
            throw new BusinessException("Percentage discount not valid");
        }
        while (this.coupons.existsByCode(couponCode = this.generateAlphanumericCouponCode())) {
        }
        Coupon coupon = new Coupon();
        coupon.setShop(shop);
        coupon.setPercentageDiscount(request.getPercentageDiscount());
        coupon.setCode(couponCode);
        this.coupons.save(coupon);
        return couponCode;
    }

    public double getCouponDiscount(String couponCode, double amount, String cartId) {
        Coupon coupon = (Coupon)this.coupons.findByCode(couponCode).orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
        return 0.0;
    }

    private String generateAlphanumericCouponCode() {
        int i;
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        ArrayList<Character> pool = new ArrayList<Character>();
        for (i = 0; i < 4; ++i) {
            pool.add(Character.valueOf(letters.charAt(random.nextInt(letters.length()))));
        }
        for (i = 0; i < 2; ++i) {
            pool.add(Character.valueOf(numbers.charAt(random.nextInt(numbers.length()))));
        }
        Collections.shuffle(pool);
        Iterator iterator = pool.iterator();
        while (iterator.hasNext()) {
            char c = ((Character)iterator.next()).charValue();
            code.append(c);
        }
        return code.toString();
    }

    @Generated
    public CouponServiceImpl(ShopRepository shopRepository, CouponRepository coupons) {
        this.shopRepository = shopRepository;
        this.coupons = coupons;
    }
}

