package dev.gagnon.repository;

import dev.gagnon.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CouponRepository
extends JpaRepository<Coupon, Long> {
    public boolean existsByCode(String var1);

    @Query(value="select c from Coupon c where c.code=:couponCode")
    public Optional<Coupon> findByCode(String couponCode);
}

