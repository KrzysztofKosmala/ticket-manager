package pl.ticket.payment.service;

import org.springframework.stereotype.Service;
import pl.ticket.dto.OrderEvent;
import pl.ticket.payment.service.p24.fakePayment.PaymentInitializer;

@Service
public class PaymentService {
    private final PaymentClientService paymentClientService;
    private final SagaPaymentProcessService sagaPaymentProcessService;

    public PaymentService(PaymentClientService paymentClientService, SagaPaymentProcessService sagaPaymentProcessService) {
        this.paymentClientService = paymentClientService;
        this.sagaPaymentProcessService = sagaPaymentProcessService;
    }

    public String initPayment(OrderEvent orderCreated) {
        PaymentInitializer payment = paymentClientService.getInstance();
        return payment.initPayment(orderCreated);
    }

    public boolean verifyPayment(OrderEvent orderCreated) {
        PaymentInitializer payment = paymentClientService.getInstance();
        boolean isOrderPaid = payment.verifyPayment(orderCreated);
        if(isOrderPaid){
            sagaPaymentProcessService.publishPaymentCompleted(orderCreated);
            return true;
        }
        sagaPaymentProcessService.publishPaymentRejected(orderCreated);
        return false;
    }
}
