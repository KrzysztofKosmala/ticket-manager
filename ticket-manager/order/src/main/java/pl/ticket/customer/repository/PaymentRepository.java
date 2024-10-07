package pl.ticket.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ticket.customer.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>
{
}
