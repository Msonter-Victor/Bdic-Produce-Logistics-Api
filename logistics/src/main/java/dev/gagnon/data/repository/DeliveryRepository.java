package dev.gagnon.data.repository;

import dev.gagnon.data.model.DeliveryOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<DeliveryOrder, Long> {
}
