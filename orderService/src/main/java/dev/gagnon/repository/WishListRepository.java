package dev.gagnon.repository;

import dev.gagnon.model.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WishListRepository
extends JpaRepository<WishList, Long> {
    @Query(value="select w from WishList w where w.buyerEmail=:buyerEmail")
    Optional<WishList> findByBuyerEmail(String buyerEmail);
}

