package dev.gagnon.repository;

import dev.gagnon.model.WishListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WishListItemRepository
extends JpaRepository<WishListItem, Long> {
    @Query(value="select w from WishListItem w where w.wishlist.id=:id")
    List<WishListItem> findByWishlistId(Long id);

    @Query(value="select w from WishListItem w where w.productId=:productId")
    Optional<WishListItem> findByProductId(Long productId);
    @Query(value="select w from WishListItem w where w.productId=:productId and w.wishlist.buyerEmail=:buyerEmail")
    Optional<WishListItem> findByProductIdAndUser(Long productId, String buyerEmail);
}

