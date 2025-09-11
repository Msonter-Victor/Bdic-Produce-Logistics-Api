// PaymentTransactionRepository.java
package dev.gagnon.data.repository;

import dev.gagnon.data.constants.PaymentType;
import dev.gagnon.data.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByReference(String reference);
    @Query("select p from PaymentTransaction p where p.paymentType=:paymentType")
    List<PaymentTransaction> findByPaymentType(PaymentType paymentType);

}