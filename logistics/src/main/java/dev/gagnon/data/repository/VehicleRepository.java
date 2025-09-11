package dev.gagnon.data.repository;

import dev.gagnon.data.constants.RideCurrentStatus;
import dev.gagnon.data.constants.Type;
import dev.gagnon.data.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    @Query("select v from Vehicle v where v.company.ownerEmail=:email")
    List<Vehicle> findAllByOwnerEmail(String email);
    @Query("select v from Vehicle v where v.company.ownerEmail=:email and v.type=:type")
    List<Vehicle> findAllTrucksByOwnerEmail(@Param("email") String email, @Param("type") Type type);
    @Query("select v from Vehicle v where v.company.ownerEmail=:email and  v.type=:type")
    List<Vehicle> findAllBikesByOwnerEmail(String email, Type type);
    @Query("select v from Vehicle v where v.status=:rideCurrentStatus")
    List<Vehicle> findAllAvailable(RideCurrentStatus rideCurrentStatus);
}
