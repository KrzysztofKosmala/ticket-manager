package pl.ticket.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ticket.payment.model.PaymentOrderStatus;
import pl.ticket.payment.model.PaymentStatus;
import pl.ticket.payment.repository.PaymentOrderStatusRepository;

import java.util.Optional;

@Slf4j
@Service
public class PaymentOrderStatusService {
    private final PaymentOrderStatusRepository paymentOrderStatusRepository;

    public PaymentOrderStatusService(PaymentOrderStatusRepository paymentOrderStatusRepository) {
        this.paymentOrderStatusRepository = paymentOrderStatusRepository;
    }

    public void savePayment(PaymentOrderStatus paymentOrderStatus){
        log.info("Zapis płatności do bazy danych");
        paymentOrderStatusRepository.save(paymentOrderStatus);
        log.info("Płatność zapisana");
    }

    public void changePaymentStatusForOrderId(Long orderId, PaymentStatus status) {
        log.info("@#@ Change status payment");
        PaymentOrderStatus paymentOrder = paymentOrderStatusRepository
                .findByOrderId(orderId);
        paymentOrder.setPaymentStatus(status);
        paymentOrderStatusRepository.save(paymentOrder);
    }

    public PaymentOrderStatus findByOrderId(Long orderId){
        return paymentOrderStatusRepository.findByOrderId(orderId);

    }
}
