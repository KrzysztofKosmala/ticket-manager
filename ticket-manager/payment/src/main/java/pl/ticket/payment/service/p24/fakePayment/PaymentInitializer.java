package pl.ticket.payment.service.p24.fakePayment;

import pl.ticket.dto.OrderEvent;
import pl.ticket.payment.model.PaymentOrderStatus;

public interface PaymentInitializer {
    String initPayment(OrderEvent orderEvent);
    boolean verifyPayment(PaymentOrderStatus paymentOrderStatus);
}
