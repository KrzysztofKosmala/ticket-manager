package pl.ticket.payment.service.p24.fakePayment;

import org.springframework.stereotype.Service;
import pl.ticket.dto.OrderEvent;

@Service
public class FakePayment24Service implements PaymentInitializer {

    @Override
    public String initPayment(OrderEvent orderEvent) {
        return "https://fake.sandbox.przelewy24.pl/" + orderEvent.hashCode();
    }

    @Override
    public boolean verifyPayment(OrderEvent orderEvent) {
        return false;
    }
}
