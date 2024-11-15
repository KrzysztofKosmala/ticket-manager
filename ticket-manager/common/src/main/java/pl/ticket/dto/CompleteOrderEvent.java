package pl.ticket.dto;

import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CompleteOrderEvent
{
    private Long orderId;
    private String message;
    private String clientEmail;
    List<ConcreteTicketDto> concreteTickets;
}
