package pl.ticket.event.customer.ticket.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDto {
    private String eventName; //
    /*TODO: ticketlist sugeruje że w środku jest lista chyba powinno być coś w stylu List<ticketDto>*/
    private List<TicketListDto> tickets;
}
