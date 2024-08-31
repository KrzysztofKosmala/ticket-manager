package pl.ticket.event.customer.ticket.model.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ticket.event.customer.ticket.model.TicketType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketListDto {
    @Enumerated(EnumType.STRING)
    private TicketType type;
    private BigDecimal price;
    private int amount;
    private LocalDate date;
    private LocalTime time;
}
