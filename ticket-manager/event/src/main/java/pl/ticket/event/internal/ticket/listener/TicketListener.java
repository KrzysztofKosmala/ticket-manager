package pl.ticket.event.internal.ticket.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.ticket.dto.CompleteOrderEvent;
import pl.ticket.dto.ConcreteTicketDto;
import pl.ticket.dto.OrderEvent;
import pl.ticket.event.internal.ticket.model.InternalConcreteTicket;
import pl.ticket.event.internal.ticket.repository.InternalConcreteTicketRepository;
import pl.ticket.event.internal.ticket.service.InternalConcreteTicketService;
import pl.ticket.event.internal.ticket.service.InternalTicketService;
import pl.ticket.event.internal.ticket.service.SagaReservationProcessService;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class TicketListener
{

        private final InternalTicketService internalTicketService;
        private final InternalConcreteTicketService internalConcreteTicketService;
        private final SagaReservationProcessService sagaReservationProcessService;
        private final InternalConcreteTicketRepository internalConcreteTicketRepository;

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

        @RabbitListener(queues = "${rabbitmq.order-queue.prepareConcreteTickets}", errorHandler = "ticketValidationExceptionHandler")
        public void handlePrepareConcreteTickets(OrderEvent orderEvent)
        {
                //TODO: co z rollbackiem jak tutaj sie wysypie?
                log.info("Received event to prepare concrete tickets, event: {}", orderEvent.toString());
                List<InternalConcreteTicket> concreteTickets = internalConcreteTicketService.createConcreteTickets(orderEvent.getOrderRows());
                List<ConcreteTicketDto> concreteTicketsDto = internalConcreteTicketRepository.findConcreteTicketDtosByGeneralTicketIds(concreteTickets.stream().map(InternalConcreteTicket::getId).toList());
                sagaReservationProcessService.publishPreparedConcreteTickets(CompleteOrderEvent.builder()
                                .orderId(orderEvent.getOrderId())
                                .clientEmail(orderEvent.getClientEmail())
                                .message(orderEvent.getMessage())
                                .concreteTickets(concreteTicketsDto)
                        .build());
        }
}
