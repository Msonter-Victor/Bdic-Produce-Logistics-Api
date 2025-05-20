package dev.gagnon.Repository;

import dev.gagnon.Model.CouncilWard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouncilWardRepository extends JpaRepository<CouncilWard, Long> {
}
