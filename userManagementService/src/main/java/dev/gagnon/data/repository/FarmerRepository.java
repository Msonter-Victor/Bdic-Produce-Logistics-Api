package dev.gagnon.data.repository;

import dev.gagnon.data.model.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FarmerRepository extends JpaRepository<Farmer, UUID> {
}
