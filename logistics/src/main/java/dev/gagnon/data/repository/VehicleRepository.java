package dev.gagnon.data.repository;

import dev.gagnon.data.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    @Query("select v from Vehicle v where v.company.ownerEmail=:email")
    List<Vehicle> findAllByOwnerEmail(String email);
    @Query("select v from Vehicle v where v.company.ownerEmail=:email and v.type=:TRUCK")
    List<Vehicle> findAllTrucksByOwnerEmail(String email);
    @Query("select v from Vehicle v where v.company.ownerEmail=:email and v.type=:BIKE")
    List<Vehicle> findAllBikesByOwnerEmail(String email);
}
