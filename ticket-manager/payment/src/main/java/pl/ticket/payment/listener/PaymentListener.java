package pl.ticket.payment.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.ticket.dto.OrderEvent;
import pl.ticket.payment.service.PaymentService;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentListener {
    private final PaymentService paymentService;

    @RabbitListener(queues = "${rabbitmq.order-queue.orderReserved}")
    public void consumeOrderCreated(OrderEvent orderEvent) {
        log.info("Received event to init the order payment, event: {}", orderEvent.toString());
        paymentService.initPayment(orderEvent);
    }
}
