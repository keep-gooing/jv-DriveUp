package carsharingapp.repository;

import carsharingapp.model.Payment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Page<Payment> findAllByRentalUserId(Long searchableId, Pageable pageable);

    Optional<Payment> findBySessionId(String sessionId);
}
