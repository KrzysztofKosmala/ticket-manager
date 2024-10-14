package pl.ticket.event.internal.ticket.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.dto.OrderEvent;
import pl.ticket.dto.OrderRowDto;
import pl.ticket.event.customer.ticket.model.InternalTicket;
import pl.ticket.event.customer.ticket.model.Ticket;
import pl.ticket.event.customer.ticket.repository.TicketRepository;
import pl.ticket.event.customer.ticket.service.TicketService;
import pl.ticket.event.internal.ticket.exception.ReservationProcessException;
import pl.ticket.event.internal.ticket.repository.InternalTicketRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InternalTicketService
{
    private final InternalTicketRepository internalTicketRepository;
    private final SagaReservationProcessService sagaReservationProcessService;
    private final TicketRepository ticketRepository;

    @Transactional
    public void reserveTickets(OrderEvent order)
    {
        for (OrderRowDto orderRow : order.getOrderRows()) {
            InternalTicket ticket = internalTicketRepository.findById(orderRow.getProductId())
                    .orElseThrow(() -> new ReservationProcessException("Ticket not found for order ID: " + order.getOrderId()));

            if (ticket.getAmount() < orderRow.getQuantity()) {
                throw new ReservationProcessException("Not enough tickets for order ID: " + order.getOrderId());
            }

            // Rezerwacja biletów
            ticket.setAmount(ticket.getAmount() - orderRow.getQuantity());
            internalTicketRepository.save(ticket);

        }
        //publish to queue reservation complete
        sagaReservationProcessService.publishReservationCompleted(order);
    }
}
