package dev.gagnon.Repository;

import dev.gagnon.Model.Market;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface MarketRepository extends JpaRepository<Market, Long> {
    @Query("select m from Market m where m.name=:marketName")
    Optional<Market> findByName(Long marketName);
}
