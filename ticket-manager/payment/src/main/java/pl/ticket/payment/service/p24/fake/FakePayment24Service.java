package pl.ticket.payment.service.p24.fake;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import pl.ticket.dto.EmailMessage;
import pl.ticket.dto.OrderEvent;
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
public class FakePayment24Service implements PaymentInitializer {
    private final SagaPaymentProcessService sagaPaymentProcessService;
    private final PaymentOrderStatusService paymentOrderStatusService;
    private final static String PAYMENT_URL = "localhost:8082/api/v1/payments/";
    private final TaskScheduler scheduler;
    private ScheduledFuture<?> scheduledTask;

    public FakePayment24Service(SagaPaymentProcessService sagaPaymentProcessService,
                                PaymentOrderStatusService paymentOrderStatusService) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.initialize();
        this.sagaPaymentProcessService = sagaPaymentProcessService;
        this.scheduler = taskScheduler;
        this.paymentOrderStatusService = paymentOrderStatusService;
    }

    private String generateFakeToken(){
        return UUID.randomUUID().toString();
    }

    @Override
    public void initPayment(OrderEvent orderEvent) {

        String paymentUrl = PAYMENT_URL + orderEvent.getOrderId();

        //zapisac do bazy orderId + status platnosci na PENDING + link
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
        startTaskWithTimeout(paymentOrderStatusService,
                orderEvent, 20000, 180000);
    }
    private boolean checkStatusPayment(PaymentStatus paymentStatus){
        if (paymentStatus.equals(PaymentStatus.PENDING)){
            return false;
        }
        return true;
    }

    private void sendMail(OrderEvent orderEvent, String paymentUrl) {
        EmailMessage emailMessage = EmailMessageGenerator.payOrderMessage(orderEvent, paymentUrl);
        sagaPaymentProcessService.publishEmailPayment(emailMessage);
    }

    public PaymentStatus simulateOrderPayment(){
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
            log.info("Wykonanie zadania: " + System.currentTimeMillis());

            PaymentOrderStatus paymentOrderStatus = paymentOrderStatusService.findByOrderId(orderEvent.getOrderId());
            boolean isPaid = checkStatusPayment(paymentOrderStatus.getPaymentStatus());

            // Zakończ zadanie po określonym czasie lub jak płatność jest opłacone
            if (elapsedTime >= timeoutMillis || isPaid) {
                log.info("Zadanie zakończone po osiągnięciu limitu czasu.");
                stopTask();
                publishPaymentStatus(orderEvent, isPaid);
            }
        };
        scheduledTask = scheduler.scheduleAtFixedRate(task, intervalMillis);
        log.info("Zadanie zostało uruchomione!");
    }

    private void stopTask() {
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
            log.info("Zadanie zostało zatrzymane.");
        }
    }

    private void publishPaymentStatus(OrderEvent orderEvent, boolean isPaid){
        if(isPaid){
            sagaPaymentProcessService.publishPaymentCompleted(orderEvent);
        } else {
            sagaPaymentProcessService.publishPaymentRejected(orderEvent);
        }
    }
}
