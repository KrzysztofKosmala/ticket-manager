package pl.ticket.cart.customer.model.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartTicketDto
{
    private int quantity;
    private TicketDto ticket;
}
