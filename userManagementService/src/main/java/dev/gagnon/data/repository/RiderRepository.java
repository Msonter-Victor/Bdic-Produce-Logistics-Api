package dev.gagnon.data.repository;

import dev.gagnon.data.model.Rider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RiderRepository extends JpaRepository<Rider, UUID> {
}
