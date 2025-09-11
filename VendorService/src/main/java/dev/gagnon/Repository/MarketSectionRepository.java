package dev.gagnon.Repository;

import dev.gagnon.Model.Market;
import dev.gagnon.Model.MarketSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketSectionRepository extends JpaRepository<MarketSection, Long> {
    MarketSection findByName(String marketSection);
    @Query("select m from MarketSection m where m.market.id=:marketId")
    List<MarketSection> findAlByMarket(Long marketId);
}
