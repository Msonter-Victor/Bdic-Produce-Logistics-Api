package dev.gagnon.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "delivery")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private Integer statusId;
    private String trackingNumber;
    private String deliveryConfirmationCode;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private DeliveryAgent agent;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
