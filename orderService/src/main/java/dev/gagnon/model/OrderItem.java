package dev.gagnon.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
    
    private Long productId;
    
    private Integer quantity;
    
//    private BigDecimal unitPrice;
//
//    private String productName;
//    private String productImage;
//
//    public BigDecimal getTotalPrice() {
//        return unitPrice.multiply(BigDecimal.valueOf(quantity));
//    }
}