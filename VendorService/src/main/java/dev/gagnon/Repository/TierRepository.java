package dev.gagnon.Repository;

import dev.gagnon.Model.ShopTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface TierRepository extends JpaRepository<ShopTier,Long> {
}
