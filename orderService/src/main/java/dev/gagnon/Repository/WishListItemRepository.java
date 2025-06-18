package dev.gagnon.repository;

import dev.gagnon.model.WishListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WishListItemRepository extends JpaRepository<WishListItem, Long> {
    @Query("select w from WishListItem w where w.wishlist.id=:id")
    List<WishListItem> findByWishlistId(long id);
}
