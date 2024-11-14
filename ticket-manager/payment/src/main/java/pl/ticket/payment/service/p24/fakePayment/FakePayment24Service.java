package pl.ticket.payment.service.p24.fakePayment;

import org.springframework.stereotype.Service;
import pl.ticket.dto.OrderEvent;
import pl.ticket.payment.model.PaymentOrderStatus;
import pl.ticket.payment.model.PaymentStatus;
import pl.ticket.payment.repository.PaymentOrderStatusRepository;
import pl.ticket.payment.service.PaymentOrderStatusService;

import java.util.UUID;

@Service
public class FakePayment24Service implements PaymentInitializer {


    private String generateFakeToken(){
        return UUID.randomUUID().toString();
    }

    @Override
    public String initPayment(OrderEvent orderEvent) {
        return "https://fake.sandbox.przelewy24.pl/" + generateFakeToken();
    }

    @Override
    public boolean verifyPayment(PaymentOrderStatus paymentOrderStatus) {
        PaymentStatus paymentStatus = paymentOrderStatus.getPaymentStatus();

        if (paymentStatus.equals(PaymentStatus.PAID)){
            return true;
        }else if(paymentStatus.equals(PaymentStatus.REJECTED)){
            return false;
        }
        return false;
    }
}
