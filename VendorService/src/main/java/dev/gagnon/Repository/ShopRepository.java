package dev.gagnon.Repository;

import dev.gagnon.Model.Shop;
import dev.gagnon.Model.constants.PromotedStatus;
import dev.gagnon.Model.constants.PromotionType;
import dev.gagnon.Model.constants.ShopStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    @Query("select s from Shop s where s.user.email=:email")
    Optional<Shop> getShopsByEmail(String email);
    @Query("select s from Shop s where s.market.id=:id")
    List<Shop> findAllByMarketId(Long id);
    @Query("select s from Shop s where s.marketSection.id=:id")
    List<Shop> findAllBySectionId(Long id);
    @Query("SELECT s FROM Shop s WHERE s.promotedStatus = :promotedStatus AND s.lastPromotedAt <= :expiredDate")
    List<Shop> findByPromotedStatusAndLastPromotedAtBefore(@Param("promotedStatus") PromotedStatus promotedStatus, @Param("expiredDate") LocalDateTime expiredDate);
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Shop s WHERE s.user.email = :email")
    boolean existsByEmail(@Param("email") String email);
    @Query("select s from Shop s where s.promotedStatus=:promotedStatus")
    List<Shop> findPromotedShops(PromotedStatus promotedStatus);
    @Query("select s from Shop s where s.status=:status")
    List<Shop> findActiveShops(ShopStatus status);
    @Query("select s from Shop s where s.shopTier=:tier")
    List<Shop> findByShopTier(@Param("tier") dev.gagnon.Model.ShopTier tier);
    
    @Modifying
    @Transactional
    @Query("delete from Shop s where s.user.id = :userId")
    int deleteByUserId(Long userId);

    @Query("select s from Shop s where s.shopTier.tierName=:tier")
    List<Shop> findPlatinumShops(String tier);
}