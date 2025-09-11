package dev.gagnon.Repository;

import dev.gagnon.Model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("select r from Review r where r.product.id=:id")
    List<Review> findByProductId(Long id);
    @Query("select r from Review r where r.reviewer.email=:email and r.product.id=:productId")
    Review findByReviewerAndProduct(String email, Long productId);
    @Query("select r from Review r where r.product.shop.id=:shopId")
    List<Review> findByShopId(Long shopId);
}
