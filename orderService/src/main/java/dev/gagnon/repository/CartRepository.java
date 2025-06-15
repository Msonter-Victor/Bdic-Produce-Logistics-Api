package dev.gagnon.repository;

import dev.gagnon.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("select c from Cart c where c.cartId=:cartId or c.buyerEmail=:buyerEmail")
    Optional<Cart> findByCartIdOrBuyerEmail(String cartId, String buyerEmail);
    @Query("select c from Cart c where c.buyerEmail=:buyerEmail")
    Optional<Cart> findByBuyerEmail(String buyerEmail);
    @Query("select c from Cart c where c.cartId=:cartId")
    Optional<Cart> findByCartId(String cartId);
}