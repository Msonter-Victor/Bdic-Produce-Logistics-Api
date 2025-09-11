package dev.gagnon.model;

import com.pgvector.PGvector;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_embeddings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductEmbedding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long productId;
    
    @Column(nullable = false)
    private String productName;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column
    private String category;
    
    @Column
    private String subCategory;
    
    @Column
    private String market;
    
    @Column
    private String shopName;
    
    @Column
    private Long shopId;
    
    @Column(columnDefinition = "vector(1536)")
    private PGvector embedding;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}