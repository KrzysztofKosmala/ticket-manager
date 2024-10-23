package pl.ticket.customer.listner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.ticket.customer.service.OrderService;
import pl.ticket.dto.OrderEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderListener
{
    private final OrderService orderService;

    @RabbitListener(queues = "${rabbitmq.order-queue.reservationCompleted}")
    public void consumeReservationCompleted(OrderEvent orderEvent)
    {
        log.info("Received event to update order status to reserved, event: {}", orderEvent.toString());
        orderService.changeStatusToReserved(orderEvent);

    }

    @RabbitListener(queues = "${rabbitmq.order-queue.reservationRejected}")
    public void consumeReservationRejected(OrderEvent orderEvent)
    {
        log.info("Received event to update order status to rejected, event: {}", orderEvent.toString());
        orderService.changeStatusToCanceled(orderEvent);

    }

    @RabbitListener(queues = "${rabbitmq.order-queue.paymentCompleted}")
    public void consumePaymentCompleted(OrderEvent orderEvent)
    {
        log.info("Received event to update order status to completed, event: {}", orderEvent.toString());
        orderService.changeStatusToCompleted(orderEvent);
    }

    @RabbitListener(queues = "${rabbitmq.order-queue.paymentRejected}")
    public void consumePaymentRejected(OrderEvent orderEvent)
    {
        log.info("Received event to update order status to processing, event: {}", orderEvent.toString());
        orderService.unbookOrder(orderEvent);
    }


    @RabbitListener(queues = "${rabbitmq.order-queue.orderUnbooked}")
    public void consumeOrderUnbooked(OrderEvent orderEvent)
    {
        log.info("Received event to update order status to canceled, event: {}", orderEvent.toString());
        orderService.cancelOrder(orderEvent);
    }
}
