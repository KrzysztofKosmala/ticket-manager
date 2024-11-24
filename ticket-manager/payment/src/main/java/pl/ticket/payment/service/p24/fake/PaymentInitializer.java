package pl.ticket.payment.service.p24.fake;

import pl.ticket.dto.OrderEvent;

public interface PaymentInitializer {
    void initPayment(OrderEvent orderEvent);
    void verifyPayment(OrderEvent orderEvent);
}
