package pl.ticket.payment.service;

import org.springframework.stereotype.Service;
import pl.ticket.payment.service.p24.fakePayment.PaymentInitializer;
import pl.ticket.payment.model.OrderCreated;

@Service
public class PaymentService {
    private final PaymentClientService paymentClientService;

    public PaymentService(PaymentClientService paymentClientService) {
        this.paymentClientService = paymentClientService;
    }

    public String initPayment(OrderCreated orderCreated) {
        PaymentInitializer payment = paymentClientService.getInstance();
        return payment.initPayment(orderCreated);
    }
}
