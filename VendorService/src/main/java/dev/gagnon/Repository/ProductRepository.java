package dev.gagnon.Repository;


import com.netflix.spectator.api.Registry;
import dev.gagnon.Model.Product;
import dev.gagnon.Model.constants.FeaturedStatus;
import dev.gagnon.Model.constants.MarketStatus;
import dev.gagnon.Model.constants.ShopStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> , JpaSpecificationExecutor<Product> {
    @Query("select p from Product p where p.shop.user.email=:email")
    List<Product> findByUserEmail(String email);
    @Query("select p from Product p where p.category.id=:categoryId and p.shop.status=:status and p.shop.marketSection.status=:lineStatus and p.shop.market.status=:marketStatus")
    List<Product> findByCategory(Long categoryId,ShopStatus status, MarketStatus lineStatus, MarketStatus marketStatus);
    @Query("select p from Product p where p.shop.id=:shopId")
    List<Product> findAllByShop(Long shopId);
    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.category.name) LIKE LOWER(CONCAT('%', :keyword, '%')) and p.shop.status=:status and p.shop.marketSection.status=:lineStatus and p.shop.market.status=:marketStatus")
    List<Product> searchAllBy(String keyword,ShopStatus status, MarketStatus lineStatus, MarketStatus marketStatus);
    @Query("select p from Product p where p.category.name=:categoryName and p.shop.marketSection.status=:lineStatus and p.shop.market.status=:marketStatus and p.shop.status=:shopStatus")
    List<Product> searchAllByCategory(String categoryName, MarketStatus lineStatus, MarketStatus marketStatus,ShopStatus shopStatus);
    @Query("select p from Product p where p.shop.market.id=:marketId")
    List<Product> findByMarket(Long marketId);
    @Query("select p from Product p where p.subCategory.id=:subCategoryId and p.shop.marketSection.status=:lineStatus and p.shop.market.status=:marketStatus and p.shop.status=:shopStatus")
    List<Product> findBySubCategory(Long subCategoryId, MarketStatus lineStatus, MarketStatus marketStatus, ShopStatus shopStatus);
    @Query("select p from Product p where p.subCategory.name=:subCategoryName and p.shop.marketSection.status=:lineStatus and p.shop.market.status=:marketStatus and p.shop.status=:shopStatus")
    List<Product> findBySubCategoryName(String subCategoryName, MarketStatus lineStatus, MarketStatus marketStatus, ShopStatus shopStatus);
    @Query("select p from Product p where p.featuredStatus=:featuredStatus")
    List<Product> findAllByFeatured(FeaturedStatus featuredStatus);
    @Query("select p from Product p where p.featuredStatus=:featuredStatus and p.shop.id=:shopId and p.shop.status=:status")
    List<Product> findAllByFeaturedAndShop(FeaturedStatus featuredStatus, Long shopId, ShopStatus status);
    @Query("select p from Product p where p.isFloated=:b")
    List<Product> findAllByFloated(boolean b);
    @Query("select p from Product p where p.isPromoted=:b")
    List<Product> findAllByPromoted(boolean b);
    @Query("SELECT p FROM Product p WHERE p.isPromoted = :isPromoted AND p.promotedAt <= :expiredDate")
    List<Product> findByPromotedStatusAndLastPromotedAtBefore(@Param("isPromoted") boolean isPromoted, @Param("expiredDate") LocalDateTime expiredDate);
    
    @Query("SELECT p FROM Product p WHERE p.isFloated = :isFloated AND p.floatedAt <= :expiredDate")
    List<Product> findByFloatedStatusAndLastPromotedAtBefore(@Param("isFloated") boolean isFloated, @Param("expiredDate") LocalDateTime expiredDate);
    
    @Query("SELECT p FROM Product p WHERE p.featuredStatus = :featuredStatus AND p.featuredAt <= :expiredDate")
    List<Product> findByFeaturedStatusAndFeaturedAtBefore(@Param("featuredStatus") FeaturedStatus featuredStatus, @Param("expiredDate") LocalDateTime expiredDate);
    
    // Methods for AI service with proper fetch joins to avoid lazy loading issues
    @Query("SELECT p FROM Product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.subCategory " +
           "LEFT JOIN FETCH p.shop s " +
           "LEFT JOIN FETCH s.market " +
           "ORDER BY p.id")
    List<Product> findAllWithRelations();
    
    @Query("SELECT p FROM Product p " +
           "LEFT JOIN FETCH p.category c " +
           "LEFT JOIN FETCH p.subCategory sc " +
           "LEFT JOIN FETCH p.shop s " +
           "LEFT JOIN FETCH s.market " +
           "WHERE LOWER(p.name) LIKE CONCAT('%', :query, '%') " +
           "OR LOWER(p.description) LIKE CONCAT('%', :query, '%') " +
           "OR LOWER(c.name) LIKE CONCAT('%', :query, '%') " +
           "OR LOWER(sc.name) LIKE CONCAT('%', :query, '%') " +
           "ORDER BY p.id")
    List<Product> findBySearchQueryWithRelations(@Param("query") String query);
    @Query("select p from Product p where p.id=:id and p.shop.status=:shopStatus and p.shop.marketSection.status=:lineStatus and p.shop.market.status=:marketStatus")
    Optional<Product> findByIdAndActiveShop(Long id, ShopStatus shopStatus, MarketStatus lineStatus, MarketStatus marketStatus);
    @Query("select p from Product p where p.shop.status=:shopStatus and p.shop.marketSection.status=:lineStatus and p.shop.market.status=:marketStatus")
    List<Product> findAllFromVerifiedVendors(ShopStatus shopStatus, MarketStatus lineStatus, MarketStatus marketStatus);
}
