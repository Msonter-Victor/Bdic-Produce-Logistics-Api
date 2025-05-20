package dev.gagnon.Repository;

import dev.gagnon.Model.MarketSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketSectionRepository extends JpaRepository<MarketSection, Long> {
}
