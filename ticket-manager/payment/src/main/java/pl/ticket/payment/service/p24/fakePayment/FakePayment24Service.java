package pl.ticket.payment.service.p24.fakePayment;

import org.springframework.stereotype.Service;
import pl.ticket.payment.model.OrderCreated;

@Service
public class FakePayment24Service implements PaymentInitializer {

    @Override
    public String initPayment(OrderCreated order) {
        return "https://fake.sandbox.przelewy24.pl/" + order.hashCode();
    }
}
