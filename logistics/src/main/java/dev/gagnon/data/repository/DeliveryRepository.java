package dev.gagnon.data.repository;

import dev.gagnon.data.model.DeliveryOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<DeliveryOrder, Long> {
    @Query("select d from DeliveryOrder d where d.rider.company.ownerEmail=:ownerEmail")
    List<DeliveryOrder> findByCompany(String ownerEmail);
}
