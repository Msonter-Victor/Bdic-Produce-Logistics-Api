package dev.gagnon.data.repository;

import dev.gagnon.data.model.Rider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderRepository extends JpaRepository<Rider, Long> {
}
