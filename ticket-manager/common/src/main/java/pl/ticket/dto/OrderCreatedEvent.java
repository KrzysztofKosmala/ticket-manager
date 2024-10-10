package pl.ticket.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderCreatedEvent
{
    private Long orderId;
    private List<OrderRowDto> orderRows;
}
