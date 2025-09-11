package dev.gagnon.Repository;

import dev.gagnon.Model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AddressRepository extends JpaRepository<Address, Long> {
    @Query("select a from Address a where a.user.id=:id")
    Address findByUser(Long id);
}
