package pl.ticket.cart.customer.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CartSummaryItemDto
{
    private Long id;
    private int quantity;
    private Long ticketId;
    private BigDecimal lineValue;
}
