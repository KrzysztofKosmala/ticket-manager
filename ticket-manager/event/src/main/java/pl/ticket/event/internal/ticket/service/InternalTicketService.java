package pl.ticket.event.internal.ticket.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.dto.OrderEvent;
import pl.ticket.dto.OrderRowDto;

import pl.ticket.dto.TicketWithDetailsDto;
import pl.ticket.event.internal.ticket.exception.ReservationProcessException;
import pl.ticket.event.internal.ticket.exception.UnbookProcessException;
import pl.ticket.event.internal.ticket.model.InternalTicket;
import pl.ticket.event.internal.ticket.repository.InternalTicketRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InternalTicketService
{
    private final InternalTicketRepository internalTicketRepository;
    private final SagaReservationProcessService sagaReservationProcessService;
    private final InternalTicketMapper internalTicketMapper;

    @Transactional
    public void reserveTickets(OrderEvent order)
    {
        for (OrderRowDto orderRow : order.getOrderRows()) {
            InternalTicket ticket = internalTicketRepository.findById(orderRow.getProductId())
                    .orElseThrow(() -> new ReservationProcessException("Ticket not found for order ID: " + order.getOrderId(), order));

            if (ticket.getAmount() < orderRow.getQuantity()) {
                throw new ReservationProcessException("Not enough tickets for order ID: " + order.getOrderId(), order);
            }
            // Rezerwacja biletów
            ticket.setAmount(ticket.getAmount() - orderRow.getQuantity());
        }
        //publish to queue reservation complete
        sagaReservationProcessService.publishReservationCompleted(order);
    }

    @Transactional
    public void unbookTickets(OrderEvent order)
    {
        for (OrderRowDto orderRow : order.getOrderRows())
        {
            InternalTicket ticket = internalTicketRepository.findById(orderRow.getProductId())
                    .orElseThrow(() -> new UnbookProcessException("Ticket not found for order ID: " + order.getOrderId(), order));

            ticket.setAmount(ticket.getAmount() + orderRow.getQuantity());
            internalTicketRepository.save(ticket);
        }
        //publish to queue reservation complete
        sagaReservationProcessService.publishOrderUnbooked(order);
    }

    public List<TicketWithDetailsDto> getTicketsForIds(List<Long> ticketIds)
    {
        List<InternalTicket> tickets = internalTicketRepository.findByIdInWithEventDetails(ticketIds);
        List<TicketWithDetailsDto> list = tickets.stream()
                .map(internalTicketMapper::toTicketWithDetailsDto).toList();
        return list;
    }
}
