package dev.gagnon.repository;

import dev.gagnon.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository
extends JpaRepository<Shop, Long> {
}

