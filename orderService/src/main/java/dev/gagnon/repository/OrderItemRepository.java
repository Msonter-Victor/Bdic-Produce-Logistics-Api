/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.model.OrderItem
 *  dev.gagnon.repository.OrderItemRepository
 *  org.springframework.data.jpa.repository.JpaRepository
 */
package dev.gagnon.repository;

import dev.gagnon.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository
extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long id);
}

