package dev.gagnon.repository;

import dev.gagnon.model.Payout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PayoutRepository
extends JpaRepository<Payout, Long> {
    @Query(value="select p from Payout p where p.shop.id=:shopId")
    List<Payout> findAllByShop(Long shopId);
}

