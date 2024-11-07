package pl.ticket.payment.service;

import org.springframework.stereotype.Service;
import pl.ticket.dto.OrderEvent;
import pl.ticket.payment.service.p24.fakePayment.PaymentInitializer;

@Service
public class PaymentService {
    private final PaymentClientService paymentClientService;

    public PaymentService(PaymentClientService paymentClientService) {
        this.paymentClientService = paymentClientService;
    }

    public String initPayment(OrderEvent orderCreated) {
        PaymentInitializer payment = paymentClientService.getInstance();
        return payment.initPayment(orderCreated);
    }

    public boolean verifyPayment(OrderEvent orderCreated) {
        return false;
    }
}
