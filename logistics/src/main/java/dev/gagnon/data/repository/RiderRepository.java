package dev.gagnon.data.repository;

import dev.gagnon.data.model.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RiderRepository extends JpaRepository<Rider, Long> {
    Rider findByEmail(String email);
    @Query("select r from Rider r where r.company.ownerEmail=:email")
    List<Rider> findAllByCompany(String email);

    boolean existsByEmail(String email);
}
