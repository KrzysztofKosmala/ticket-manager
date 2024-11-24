package pl.ticket.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ticket.dto.OrderEvent;
import pl.ticket.payment.model.PaymentOrderStatus;
import pl.ticket.payment.model.PaymentStatus;
import pl.ticket.payment.service.p24.fake.FakePayment24Service;

@Slf4j
@Service
public class PaymentService {
    private final PaymentClientService paymentClientService;
    private final PaymentOrderStatusService paymentOrderStatusService;

    public PaymentService(PaymentClientService paymentClientService, PaymentOrderStatusService paymentOrderStatusService) {
        this.paymentClientService = paymentClientService;
        this.paymentOrderStatusService = paymentOrderStatusService;
    }

    public void initPayment(OrderEvent orderEvent) {
        log.info("Init payment for order: " + orderEvent.getOrderId());
        paymentClientService.getInstance().initPayment(orderEvent);
        verifyPayment(orderEvent);
    }

    public void verifyPayment(OrderEvent orderEvent) {
        log.info("Verify payment for order: " + orderEvent.getOrderId());
        paymentClientService.getInstance().verifyPayment(orderEvent);
    }


    public void simulateOrderPaymentStatus(Long orderId) {
        // zmiana statusu randomowo - symulacja oplacenie/anulownia płatności za zamowienie
        PaymentStatus paymentStatus = ((FakePayment24Service) paymentClientService.getInstance()).simulateOrderPayment();

        // zapisac status w bazie
        PaymentOrderStatus paymentOrderStatus = paymentOrderStatusService
                .findByOrderId(orderId);
        paymentOrderStatus.setPaymentStatus(paymentStatus);
        paymentOrderStatusService.savePayment(paymentOrderStatus);
    }
}
