package dev.gagnon.data.repository;

import dev.gagnon.data.model.LogisticsCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LogisticsCompanyRepository extends JpaRepository<LogisticsCompany,Long> {
    boolean existsByOwnerEmail(String ownerEmail);

    Optional<LogisticsCompany> findByOwnerEmail(String ownerEmail);
}
