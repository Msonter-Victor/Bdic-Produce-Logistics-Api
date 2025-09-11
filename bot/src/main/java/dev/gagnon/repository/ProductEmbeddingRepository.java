package dev.gagnon.repository;

import dev.gagnon.model.ProductEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductEmbeddingRepository extends JpaRepository<ProductEmbedding, Long> {
    
    Optional<ProductEmbedding> findByProductId(Long productId);
    
    @Query(value = """
        SELECT * FROM product_embeddings 
        ORDER BY embedding <-> CAST(:queryEmbedding AS vector) 
        LIMIT :limit
        """, nativeQuery = true)
    List<ProductEmbedding> findSimilarProducts(
        @Param("queryEmbedding") String queryEmbedding,
        @Param("limit") int limit);
    
    @Query(value = """
        SELECT * FROM product_embeddings 
        WHERE market ILIKE %:market%
        ORDER BY embedding <-> CAST(:queryEmbedding AS vector) 
        LIMIT :limit
        """, nativeQuery = true)
    List<ProductEmbedding> findSimilarProductsInMarket(
        @Param("queryEmbedding") String queryEmbedding,
        @Param("market") String market,
        @Param("limit") int limit);
    
    @Query(value = """
        SELECT DISTINCT market FROM product_embeddings 
        WHERE market IS NOT NULL AND market != ''
        ORDER BY market
        """, nativeQuery = true)
    List<String> findAllMarkets();
    
    @Query(value = """
        SELECT DISTINCT shop_name, shop_id, market 
        FROM product_embeddings 
        WHERE market ILIKE %:market%
        ORDER BY embedding <-> CAST(:queryEmbedding AS vector) 
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findShopsWithSimilarProducts(
        @Param("queryEmbedding") String queryEmbedding,
        @Param("market") String market,
        @Param("limit") int limit);
}