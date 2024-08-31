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

    public TicketDto mapToTicketDto(Event event, List<TicketListDto> ticketListDtos){
        return TicketDto.builder()
                .eventName(event.getTitle())
                .tickets(ticketListDtos)
                .build();
    }

    public List<TicketListDto> mapToListTicketListDto(List<Ticket> tickets){
        return tickets.stream()
                .map(ticket -> TicketListDto.builder()
                        .price(ticket.getPrice())
                        .type(ticket.getType())
                        .date(ticket.getEventOccurrence().getDate())
                        .time(ticket.getEventOccurrence().getTime())
                        .amount(ticket.getAmount())
                        .build()).toList();
    }
}
