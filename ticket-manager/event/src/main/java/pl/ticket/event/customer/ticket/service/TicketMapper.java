package pl.ticket.event.customer.ticket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.ticket.event.customer.event.model.Event;
import pl.ticket.event.customer.ticket.model.Ticket;
import pl.ticket.event.customer.ticket.model.dto.TicketDto;
import pl.ticket.event.customer.ticket.model.dto.TicketListDto;

import java.util.List;

@RequiredArgsConstructor
@Component
public class TicketMapper {

    public TicketDto mapToTicketDto(Ticket ticketsByEventOccurrenceId)
    {
        return TicketDto.builder()
                .id(ticketsByEventOccurrenceId.getId())
                .price(ticketsByEventOccurrenceId.getPrice())
                .amount(ticketsByEventOccurrenceId.getAmount())
                .type(ticketsByEventOccurrenceId.getType().getType())
                .build();
    }
}
