package dev.gagnon.data.repository;

import dev.gagnon.data.model.LogisticsCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LogisticsCompanyRepository extends JpaRepository<LogisticsCompany,Long> {
    boolean existsByOwnerEmail(String ownerEmail);
    @Query("select c from LogisticsCompany c where c.ownerEmail=:ownerEmail")
    Optional<LogisticsCompany> findByOwnerEmail(String ownerEmail);
}
