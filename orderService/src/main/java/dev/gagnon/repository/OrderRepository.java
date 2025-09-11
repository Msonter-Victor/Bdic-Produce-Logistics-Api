package dev.gagnon.repository;

import dev.gagnon.model.Order;
import org.hibernate.annotations.Filter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository
extends JpaRepository<Order, Long> {
    @Query("select o from Order o where o.orderNumber=:orderNumber")
    List<Order> findByOrderNumber(String orderNumber);
    boolean existsByTransactionReference(String var1);
    @Query(value="SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.buyerEmail = :buyerEmail AND o.isParentOrder = true AND o.deleted = false ORDER BY o.createdAt DESC")
    List<Order> findByBuyerEmailAndIsParentOrderTrue(@Param(value="buyerEmail") String buyerEmail);

    @Query(value="SELECT o FROM Order o LEFT JOIN FETCH o.items LEFT JOIN FETCH o.shop LEFT JOIN FETCH o.shop.user WHERE o.parentOrderId = :parentOrderId AND o.deleted = false")
    List<Order> findByParentOrderId(@Param(value="parentOrderId") Long parentOrderId);

    @Query(value="SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.shop.id = :shopId AND o.isParentOrder = false AND o.deleted = false ORDER BY o.createdAt DESC")
    List<Order> findByShopIdAndIsParentOrderFalse(@Param(value="shopId") Long shopId);

    @Query(value="SELECT o FROM Order o WHERE o.isParentOrder = :b AND o.deleted = false")
    List<Order> findByParentOrder(@Param(value="b") boolean b);

    @Query(value="SELECT o FROM Order o WHERE o.orderNumber = :orderNumber AND o.deleted = false")
    List<Order> findAllByOrderNumber(@Param(value="orderNumber") String orderNumber);

    @Query(value="SELECT o FROM Order o WHERE o.id = :id AND o.deleted = true")
    Optional<Order> findDeletedById(@Param(value="id") Long id);

    @Query(value="SELECT o FROM Order o")
    List<Order> findAllIncludingDeleted();

    @Query(value="SELECT o FROM Order o WHERE o.buyerEmail = :buyerEmail AND o.isParentOrder = true")
    List<Order> findByBuyerEmailAndIsParentOrderTrueIncludeDeleted(@Param(value="buyerEmail") String buyerEmail);

    @Query(value="SELECT o FROM Order o WHERE o.orderNumber = :orderNumber")
    List<Order> findAllByOrderNumberIncludeDeleted(@Param(value="orderNumber") String var1);

    @Query(value="SELECT o FROM Order o WHERE o.buyerEmail = :email")
    @Filter(name="deletedFilter", condition="1=1")
    List<Order> findByEmailIncludeDeleted(@Param(value="email") String email);

    @Query(value="SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.shop.market.id = :marketId AND o.isParentOrder = true AND o.deleted = false ORDER BY o.createdAt DESC")
    List<Order> findByMarketAndIsParentOrderTrue(Long marketId);


}

