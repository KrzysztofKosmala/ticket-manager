package pl.ticket.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.ticket.payment.service.p24.fake.PaymentInitializer;

import java.util.Map;

/**
 * Serwis wybiera implementacje dla payment
 */
@Service
@RequiredArgsConstructor
public class PaymentClientService {

    @Value("${app.payments.initializer}")
    private String paymentInitializer;

    private final Map<String, PaymentInitializer> senderMap;

    public PaymentInitializer getInstance(){ // na podstawie application wybiera inicjalizacje serwisu
        if(paymentInitializer.equals("fakePayment24Service")) {
            return senderMap.get("fakePayment24Service");
        }
        return senderMap.get("paymentMethodP24");
    }
}
