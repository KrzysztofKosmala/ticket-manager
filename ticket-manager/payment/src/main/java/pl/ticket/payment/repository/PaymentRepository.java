package pl.ticket.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ticket.payment.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
