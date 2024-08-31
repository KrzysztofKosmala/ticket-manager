package pl.ticket.event.customer.event.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ticket.event.customer.ticket.model.TicketType;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventTicketDto {

    private TicketType type;
    private BigDecimal price;
    private int amount;
}
