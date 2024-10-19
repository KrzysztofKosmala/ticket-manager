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
    public void changeStatusToReserved(OrderEvent orderEvent)
    {
        log.info("Received event to update order status, event: {}", orderEvent.toString());
        orderService.changeStatusToReserved(orderEvent);

    }
}
