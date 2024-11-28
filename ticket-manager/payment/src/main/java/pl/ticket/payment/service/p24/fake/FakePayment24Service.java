package pl.ticket.payment.service.p24.fake;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import pl.ticket.dto.EmailMessage;
import pl.ticket.dto.OrderEvent;
import pl.ticket.email.EmailClient;
import pl.ticket.payment.common.mail.EmailMessageGenerator;
import pl.ticket.payment.model.PaymentOrderStatus;
import pl.ticket.payment.model.PaymentStatus;
import pl.ticket.payment.service.PaymentOrderStatusService;
import pl.ticket.payment.service.SagaPaymentProcessService;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class FakePayment24Service implements PaymentInitializer {
    private final SagaPaymentProcessService sagaPaymentProcessService;
    private final PaymentOrderStatusService paymentOrderStatusService;
    private final EmailClient emailClient;
    private final static String PAYMENT_URL = "https://localhost:8082/api/v1/payments/";
    private TaskScheduler scheduler;
    private ScheduledFuture<?> scheduledTask;
    private ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();

    private String generateFakeToken(){
        return UUID.randomUUID().toString();
    }

    @Override
    public void initPayment(OrderEvent orderEvent) {
        log.info("Tworzenie płatności za zamówienie nr: {}", orderEvent.getOrderId());
        taskScheduler.initialize();
        scheduler = taskScheduler;
        String paymentUrl = PAYMENT_URL + orderEvent.getOrderId();

        //zapisujemy orderId + status platnosci + link do platnosci
        paymentOrderStatusService.savePayment(PaymentOrderStatus.builder()
                .orderId(orderEvent.getOrderId())
                .paymentUrl(paymentUrl)
                .paymentStatus(PaymentStatus.PENDING)
                .build());

        // wyslanie maila
        sendMail(orderEvent, paymentUrl);

    }

    @Override
    public void verifyPayment(OrderEvent orderEvent) {
        log.info("Weryfikacja zamówienia nr {} " + orderEvent.getOrderId());
        startTaskWithTimeout(paymentOrderStatusService,
                orderEvent, 20000, 180000);
    }

    private void sendMail(OrderEvent orderEvent, String paymentUrl) {
        log.info("Wysyłanie e-mail z linkiem do płatności");
        EmailMessage emailMessage = EmailMessageGenerator.payOrderMessage(orderEvent, paymentUrl);
        emailClient.publishEmail(emailMessage);
    }

    public PaymentStatus simulateOrderPayment(){
        log.info("Dokonywanie płatności za zamówienie");
        return getRandomStatus();
    }

    private static PaymentStatus getRandomStatus() {
        PaymentStatus[] statuses = {PaymentStatus.PAID, PaymentStatus.REJECTED};
        int randomIndex = new Random().nextInt(statuses.length);
        return statuses[randomIndex];
    }

    public void startTaskWithTimeout(PaymentOrderStatusService paymentOrderStatusService, OrderEvent orderEvent, long intervalMillis, long timeoutMillis) {
        long startTime = System.currentTimeMillis();

        Runnable task = () -> {
            long elapsedTime = System.currentTimeMillis() - startTime;
            log.info("Sprawdzenie statusu płatności dla zamówienia nr {}", orderEvent.getOrderId());
            PaymentOrderStatus paymentOrderStatus = paymentOrderStatusService.findByOrderId(orderEvent.getOrderId());
            PaymentStatus status = paymentOrderStatus.getPaymentStatus();
            log.info("Status płatności: {}", status);


            // Zakończ zadanie po określonym czasie lub jak płatność jest opłacone
            if (elapsedTime >= timeoutMillis || status.equals(PaymentStatus.PAID) || status.equals(PaymentStatus.REJECTED)) {
                log.info("Weryfikacja została zakończona po osiągniecia maksymalnego czasu lub status zmienił się na PAID lub REJECTED");
                stopTask();
                publishPaymentStatus(orderEvent, status);

                // todo: obsluzyc status PENDING - anulowac zamowienie
            }
        };
        scheduledTask = scheduler.scheduleAtFixedRate(task, intervalMillis);
        log.info("Weryfikacja zamówienia nr {} została uruchomiona", orderEvent.getOrderId());
    }

    private void stopTask() {
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
            log.info("Weryfikacja została zakończona");
        }
    }

    private void publishPaymentStatus(OrderEvent orderEvent, PaymentStatus status){

        if(status.equals(PaymentStatus.PAID)){
            sagaPaymentProcessService.publishPaymentCompleted(orderEvent);
        } else if (status.equals(PaymentStatus.REJECTED)){
            sagaPaymentProcessService.publishPaymentRejected(orderEvent);
        }else {
            // todo: obsluzyc wyjatek
        }
    }
}
