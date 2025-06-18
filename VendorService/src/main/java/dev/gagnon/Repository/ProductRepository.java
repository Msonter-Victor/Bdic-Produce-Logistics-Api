package dev.gagnon.Repository;


import dev.gagnon.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> , JpaSpecificationExecutor<Product> {
    // Count all products by shop
    Long countByShopId(Long shopId);

    // Count all products by shop and category
    Long countByShopIdAndCategoryId(Long shopId, Long categoryId);

    // Count all products by category (across all shops)
    Long countByCategoryId(Long categoryId);

    @Query("""
    SELECT p FROM Product p
    JOIN p.category c
    JOIN p.shop s
    JOIN s.marketSection ms
    JOIN ms.market m
    WHERE
        (:categoryName IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :categoryName, '%')))
        AND (:shopName IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :shopName, '%')))
        AND (:marketName IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :marketName, '%')))
""")
    List<Product> searchProducts(
            @Param("categoryName") String categoryName,
            @Param("shopName") String shopName,
            @Param("marketName") String marketName
    );

}
