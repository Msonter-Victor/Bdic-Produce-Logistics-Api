package dev.gagnon.repository;

import dev.gagnon.model.Refund;
import dev.gagnon.model.constants.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RefundRepository
extends JpaRepository<Refund, Long> {
    @Query("select r from Refund r where r.status = :status")
    List<Refund> findAllPending(@Param("status") RefundStatus status);
}

