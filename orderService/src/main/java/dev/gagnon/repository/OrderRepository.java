package dev.gagnon.repository;

import dev.gagnon.model.Order;
import dev.gagnon.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    @Query("select o from Order o where o.orderNumber=:orderNumber and o.buyerEmail=:buyerEmail")
    Optional<Order> findByOrderNumberAndBuyerEmail(String orderNumber, String buyerEmail);
    @Query("select o from Order o where o.buyerEmail=:buyerEmail")
    List<Order> findByBuyerEmail(String buyerEmail);
}