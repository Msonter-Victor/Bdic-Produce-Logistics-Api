package dev.gagnon.repository;

import dev.gagnon.model.Call;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CallRepository extends JpaRepository<Call, Long> {
    @Query("SELECT c FROM Call c WHERE c.vendorEmail = :vendorEmail ORDER BY c.createdAt DESC")
    List<Call> findByVendorEmailOrderByCreatedAtDesc(@Param("vendorEmail") String vendorEmail);

    @Query("SELECT c FROM Call c WHERE (c.vendorEmail = :email OR c.callerEmail = :email) ORDER BY c.createdAt DESC")
    List<Call> findByUserEmailOrderByCreatedAtDesc(@Param("email") String email);
    
    @Query("SELECT c FROM Call c WHERE c.vendorEmail = :vendorEmail AND c.status = :status ORDER BY c.createdAt DESC")
    List<Call> findByVendorEmailAndStatusOrderByCreatedAtDesc(@Param("vendorEmail") String vendorEmail, @Param("status") Call.CallStatus status);
    
    @Query("SELECT c FROM Call c WHERE c.status = 'RINGING' AND c.createdAt < :timeout")
    List<Call> findExpiredRingingCalls(@Param("timeout") LocalDateTime timeout);
}