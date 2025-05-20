package dev.gagnon.Repository;

import dev.gagnon.Model.LocalGovernment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalGovernmentRepository extends JpaRepository<LocalGovernment, Long> {
}
