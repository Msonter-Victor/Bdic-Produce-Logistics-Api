package dev.gagnon.repository;

import dev.gagnon.model.Dispute;
import dev.gagnon.model.constants.DisputeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DisputeRepository
extends JpaRepository<Dispute, Long> {
    @Query(value="select d from Dispute d where d.item.id=:itemId")
    Dispute findByItemId(Long itemId);

    @Query(value="select d from Dispute d where d.item.order.shop.id=:id")
    List<Dispute> findByShop(Long id);

    @Query(value="select d from Dispute d where d.item.order.buyerEmail=:email")
    List<Dispute> findByEmail(String email);

    @Query(value="select d from Dispute d where d.status=:status")
    List<Dispute> findAllPending(DisputeStatus status);
}

