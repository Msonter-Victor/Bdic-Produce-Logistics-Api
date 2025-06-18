package dev.gagnon.repository;

import dev.gagnon.dto.request.AddToWishlistRequest;
import dev.gagnon.model.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WishListRepository extends JpaRepository<WishList, Long> {
    @Query("select w from WishList w where w.buyerEmail=:buyerEmail")
    Optional<WishList> findByBuyerEmail(String buyerEmail);
}
