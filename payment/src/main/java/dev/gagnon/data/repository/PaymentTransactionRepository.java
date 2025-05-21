// PaymentTransactionRepository.java
package dev.gagnon.data.repository;

import dev.gagnon.data.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByReference(String reference);
    Optional<PaymentTransaction> findByCredoReference(String credoReference);
}