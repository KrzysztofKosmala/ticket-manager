package pl.ticket.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ticket.dto.OrderEvent;
import pl.ticket.payment.model.PaymentOrderStatus;
import pl.ticket.payment.model.PaymentStatus;
import pl.ticket.payment.service.p24.fakePayment.PaymentInitializer;

import java.util.Random;

@Slf4j
@Service
public class PaymentService {

    private final PaymentClientService paymentClientService;
    private final PaymentOrderStatusService paymentOrderStatusService;
    private final SagaPaymentProcessService sagaPaymentProcessService;

    public PaymentService(PaymentClientService paymentClientService, PaymentOrderStatusService paymentOrderStatusService, SagaPaymentProcessService sagaPaymentProcessService) {
        this.paymentClientService = paymentClientService;
        this.paymentOrderStatusService = paymentOrderStatusService;
        this.sagaPaymentProcessService = sagaPaymentProcessService;
    }

    /**
     * symulacja tworzenia platnosci
     * ustawienie randomowego statusu
     * weryfkacja płatnosci
     */
    public String initPayment(OrderEvent orderCreated) {
        log.info("Init payment for order: " + orderCreated.getOrderId());

        PaymentInitializer payment = paymentClientService.getInstance();
        String urlPayment = payment.initPayment(orderCreated);
        paymentOrderStatusService.savePayment(PaymentOrderStatus.builder()
                        .orderId(orderCreated.getOrderId())
                        .paymentUrl(urlPayment)
                        .paymentStatus(PaymentStatus.PENDING)
                .build());

        // TODO: do zrobienia fake p24 serwis aby legitnie zasymulowac proces platnosci
        // zmiana statusu randomow - symulacja oplacenie/anulownia płatności za zamowienie
        paymentOrderStatusService.changePaymentStatusForOrderId(orderCreated.getOrderId(),
                getRandomStatus());

        // sprawdzenie statusu płatności
        verifyPayment(orderCreated);

        return urlPayment;
    }

    private static PaymentStatus getRandomStatus() {
        PaymentStatus[] statuses = {PaymentStatus.PAID, PaymentStatus.REJECTED};
        int randomIndex = new Random().nextInt(statuses.length);
        return statuses[randomIndex];
    }

    public void verifyPayment(OrderEvent orderCreated) {
        log.info("Verify payment for order: " + orderCreated.getOrderId());

        PaymentInitializer payment = paymentClientService.getInstance();
        PaymentOrderStatus paymentOrderStatus = paymentOrderStatusService
                .findByOrderId(orderCreated.getOrderId());

        boolean isOrderPaid = payment.verifyPayment(paymentOrderStatus);

        if(isOrderPaid){
            sagaPaymentProcessService.publishPaymentCompleted(orderCreated);
        }
        sagaPaymentProcessService.publishPaymentRejected(orderCreated);
    }
}
