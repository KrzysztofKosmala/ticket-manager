package pl.ticket.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ticket.payment.model.Payment;
import pl.ticket.payment.model.PaymentOrderStatus;
import pl.ticket.payment.model.PaymentStatus;

@Repository
public interface PaymentOrderStatusRepository extends JpaRepository<PaymentOrderStatus, Long> {
    PaymentOrderStatus findByOrderId(Long orderId);
}
