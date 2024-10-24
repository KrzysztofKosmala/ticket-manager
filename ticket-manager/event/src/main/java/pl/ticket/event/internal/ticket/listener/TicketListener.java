package pl.ticket.event.internal.ticket.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.ticket.dto.OrderEvent;
import pl.ticket.event.internal.ticket.service.InternalTicketService;

@Component
@Slf4j
@RequiredArgsConstructor
public class TicketListener
{

        private final InternalTicketService internalTicketService;

        @RabbitListener(queues = "${rabbitmq.order-queue.orderCreated}", errorHandler = "reservationProcessExceptionHandler")
        public void handleTicketReservation(OrderEvent orderEvent)
        {
                log.info("Received event to update ticket amount, event: {}", orderEvent.toString());
                internalTicketService.reserveTickets(orderEvent);

        }

        @RabbitListener(queues = "${rabbitmq.order-queue.unbookOrder}", errorHandler = "unbookProcessExceptionHandler")
        public void handleUnbookOrder(OrderEvent orderEvent)
        {
                log.info("Received event to unbook ticket, event: {}", orderEvent.toString());
                internalTicketService.unbookTickets(orderEvent);

        }
}
