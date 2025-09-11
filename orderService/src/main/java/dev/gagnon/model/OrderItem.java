package dev.gagnon.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.gagnon.model.constants.ItemStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name="order_items")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class OrderItem {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Version
    private Long version;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="order_id", nullable=false)
    @JsonIgnore
    private Order order;
    @Enumerated(value=EnumType.STRING)
    @Column(nullable=false, columnDefinition="VARCHAR(20) NOT NULL CHECK (status != '')")
    private ItemStatus status = ItemStatus.PENDING;
    private Long productId;
    private String productName;
    private String description;
    private String productImage;
    private Integer quantity;
    private BigDecimal unitPrice;

    public Shop getShop() {
        return this.order != null ? this.order.getShop() : null;
    }

}

