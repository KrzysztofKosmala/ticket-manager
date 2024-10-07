package pl.ticket.event.admin.event.data_provider;

import pl.ticket.event.customer.event_occurrence.model.EventOccurrence;
import pl.ticket.event.customer.ticket.model.Ticket;
import pl.ticket.event.customer.ticket.model.TicketType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TicketProvider {

    public List<Ticket> prepareTicketWithOccurrence(){

        LocalTime timeParsed = LocalTime.parse("18:00");
        LocalDate date = LocalDate.of(2024, 8, 26);

        EventOccurrence occurrence = EventOccurrence.builder()
                .id(222L)
                .eventId(111L)
                .date(date)
                .time(timeParsed)
                .isCommonPool(true)
                .build();

        Ticket ticket1 = Ticket.builder()
                .id(333)
                .price(BigDecimal.valueOf(99.99))
                .type(TicketType.FULL_PRICE)
                .amount(50)
                .eventOccurrence(occurrence)
                .build();

        Ticket ticket2 = Ticket.builder()
                .id(444)
                .price(BigDecimal.valueOf(49.99))
                .type(TicketType.HALF_PRICE)
                .amount(30)
                .eventOccurrence(occurrence)
                .build();

        return List.of(ticket1, ticket2);
    }
}
