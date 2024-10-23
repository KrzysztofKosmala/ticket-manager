package pl.ticket.payment.service.p24.fakePayment;

import pl.ticket.payment.model.OrderCreated;

public interface PaymentInitializer {
    String initPayment(OrderCreated order);
}
